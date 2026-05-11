package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.domain.error.SolicitudAdopcionEstadoInvalidoException;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
import es.refugio.refugio.domain.error.AnimalYaAdoptadoException;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AprobarSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AnimalRepository animalRepository;
    private final AdoptanteRepository adoptanteRepository;
    private final AdopcionRepository adopcionRepository;
    private final es.refugio.refugio.application.service.NotificacionService notificacionService;

    @Transactional
    public Adopcion aprobar(SolicitudAdopcionId solicitudId) {
        System.out.println("DEBUG: Iniciando AprobarSolicitudAdopcionUseCase para ID=" + solicitudId.getValue());

        // 1 — Buscar y validar la solicitud
        SolicitudAdopcion solicitud = solicitudAdopcionRepository.getById(solicitudId)
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(solicitudId.getValue()));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE
                && solicitud.getEstado() != EstadoSolicitud.EN_REVISION) {
            System.err.println("ERROR: Estado inválido para aprobar: " + solicitud.getEstado());
            throw new SolicitudAdopcionEstadoInvalidoException(solicitud.getEstado().name());
        }

        // VALIDACIÓN: ¿El animal ya tiene una adopción?
        if (adopcionRepository.existsByAnimalId(solicitud.getAnimalId())) {
            System.err.println("ERROR: El animal ya tiene una adopción activa.");
            throw new AnimalYaAdoptadoException(solicitud.getAnimalId().getValue());
        }

        // 2 — Actualizar solicitud → APROBADA
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitudAdopcionRepository.save(solicitud);
        System.out.println("DEBUG: Solicitud actualizada a APROBADA");

        // 3 — Actualizar animal → RESERVADO
        String animalNombre = animalRepository.getById(solicitud.getAnimalId())
                .map(animal -> {
                    animal.setEstado(EstadoAnimal.RESERVADO);
                    animalRepository.save(animal);
                    System.out.println("DEBUG: Animal actualizado a RESERVADO: " + animal.getNombre());
                    return animal.getNombre();
                })
                .orElse("el animal");

        // 4 — Actualizar adoptante → APROBADO e informar
        adoptanteRepository.getById(solicitud.getAdoptanteId())
                .ifPresent(adoptante -> {
                    adoptante.setEstadoValidacion(EstadoValidacion.APROBADO);
                    adoptanteRepository.save(adoptante);
                    System.out.println("DEBUG: Adoptante actualizado a APROBADO");

                    if (adoptante.getUsuarioId() != null) {
                        notificacionService.enviar(
                                adoptante.getUsuarioId(),
                                "Solicitud de Adopción Aprobada",
                                "¡Buenas noticias! Tu solicitud para adoptar a " + animalNombre + " ha sido aprobada.",
                                "ADOPCION",
                                "/web/solicitudes/" + solicitud.getId().getValue() + "/detalle");
                    }
                });

        // 5 — Crear la Adopcion con estado PENDIENTE_FIRMA
        Adopcion nuevaAdopcion = Adopcion.builder()
                .adoptanteId(solicitud.getAdoptanteId())
                .animalId(solicitud.getAnimalId())
                .solicitudAdopcionId(solicitud.getId().getValue())
                .fechaAdopcion(LocalDateTime.now())
                .estado(EstadoAdopcion.PENDIENTE_FIRMA)
                .build();

        Adopcion savedAdopcion = adopcionRepository.save(nuevaAdopcion);
        System.out.println("DEBUG: Adopción creada con éxito. ID="
                + (savedAdopcion.getId() != null ? savedAdopcion.getId().getValue() : "PENDIENTE"));

        return savedAdopcion;
    }
}
