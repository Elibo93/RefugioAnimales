package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
import es.refugio.refugio.domain.error.AnimalYaAdoptadoException;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class EditSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AdoptanteRepository adoptanteRepository;
    private final AnimalRepository animalRepository;
    private final AdopcionRepository adopcionRepository;
    private final es.refugio.refugio.application.service.NotificacionService notificacionService;

    @Transactional
    public SolicitudAdopcion update(EditSolicitudAdopcionCommand command) {
        System.out.println("DEBUG: Iniciando actualización de solicitud ID=" + command.id().getValue());
        
        return solicitudAdopcionRepository.getById(command.id())
                .map(solicitud -> {
                    EstadoSolicitud estadoAnterior = solicitud.getEstado();
                    EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(command.estado().toUpperCase());
                    
                    System.out.println("DEBUG: Estado anterior=" + estadoAnterior + ", Nuevo estado=" + estadoEnum);

                    solicitud.setFecha(command.fecha());
                    solicitud.setEstado(estadoEnum);
                    solicitud.setComentario(command.comentario());
                    solicitud.setComentarioAdmin(command.comentarioAdmin());
                    
                    SolicitudAdopcion saved = solicitudAdopcionRepository.save(solicitud);

                    // LÓGICA DE APROBACIÓN AUTOMÁTICA AL EDITAR
                    if (estadoEnum == EstadoSolicitud.APROBADA && estadoAnterior != EstadoSolicitud.APROBADA) {
                        System.out.println("DEBUG: Detectada aprobación. Creando adopción...");
                        
                        // 1. Validar si ya existe adopción para este animal
                        if (adopcionRepository.existsByAnimalId(solicitud.getAnimalId())) {
                            System.err.println("ERROR: El animal ya ha sido adoptado.");
                            throw new AnimalYaAdoptadoException(solicitud.getAnimalId().getValue());
                        }

                        // 2. Crear Adopción vinculada
                        Adopcion nuevaAdopcion = Adopcion.builder()
                                .adoptanteId(solicitud.getAdoptanteId())
                                .animalId(solicitud.getAnimalId())
                                .solicitudAdopcionId(solicitud.getId().getValue())
                                .fechaAdopcion(LocalDateTime.now())
                                .estado(EstadoAdopcion.PENDIENTE_FIRMA)
                                .build();
                        
                        Adopcion savedAdopcion = adopcionRepository.save(nuevaAdopcion);
                        System.out.println("DEBUG: Adopción guardada con ID=" + (savedAdopcion.getId() != null ? savedAdopcion.getId().getValue() : "PENDIENTE"));

                        // 3. Actualizar Animal a RESERVADO
                        animalRepository.getById(solicitud.getAnimalId()).ifPresent(animal -> {
                            animal.setEstado(EstadoAnimal.RESERVADO);
                            animalRepository.save(animal);
                            System.out.println("DEBUG: Animal actualizado a RESERVADO");
                        });

                        // 4. Actualizar Adoptante a APROBADO
                        adoptanteRepository.getById(solicitud.getAdoptanteId()).ifPresent(adoptante -> {
                            adoptante.setEstadoValidacion(EstadoValidacion.APROBADO);
                            adoptanteRepository.save(adoptante);
                            System.out.println("DEBUG: Adoptante actualizado a APROBADO");
                        });
                    }

                    // Si el estado ha cambiado, notificar al adoptante
                    if (estadoEnum != estadoAnterior) {
                        adoptanteRepository.getById(solicitud.getAdoptanteId()).ifPresent(adoptante -> {
                            if (adoptante.getUsuarioId() != null) {
                                String nombreAnimal = animalRepository.getById(solicitud.getAnimalId())
                                        .map(es.refugio.refugio.domain.model.animal.Animal::getNombre)
                                        .orElse("tu animal favorito");

                                String mensaje = "Tu solicitud de adopción para " + nombreAnimal + " ha pasado a estado: " + estadoEnum;
                                notificacionService.enviar(
                                    adoptante.getUsuarioId(),
                                    "Actualización de Solicitud",
                                    mensaje,
                                    "ADOPCION",
                                    "/web/solicitudes/" + solicitud.getId().getValue() + "/detalle"
                                );
                            }
                        });
                    }
                    
                    return saved;
                })
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(command.id().getValue()));
    }
}
