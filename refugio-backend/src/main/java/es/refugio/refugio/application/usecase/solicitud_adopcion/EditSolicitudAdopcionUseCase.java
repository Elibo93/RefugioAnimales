package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final es.refugio.refugio.domain.repository.AdoptanteRepository adoptanteRepository;
    private final es.refugio.refugio.domain.repository.AnimalRepository animalRepository;
    private final es.refugio.refugio.application.service.NotificacionService notificacionService;

    public SolicitudAdopcion update(EditSolicitudAdopcionCommand command) {
        return solicitudAdopcionRepository.getById(command.id())
                .map(solicitud -> {
                    EstadoSolicitud estadoAnterior = solicitud.getEstado();
                    EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(command.estado().toUpperCase());
                    
                    solicitud.setFecha(command.fecha());
                    solicitud.setEstado(estadoEnum);
                    solicitud.setComentario(command.comentario());
                    solicitud.setComentarioAdmin(command.comentarioAdmin());
                    
                    SolicitudAdopcion saved = solicitudAdopcionRepository.save(solicitud);

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
