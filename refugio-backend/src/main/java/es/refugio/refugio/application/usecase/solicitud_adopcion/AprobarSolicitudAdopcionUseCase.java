package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.service.NotificacionService;
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

/**
 * Caso de uso orquestador: Aprueba una solicitud de adopción.
 *
 * Pasos que realiza en una única transacción:
 * 1. Busca y valida que la solicitud está en estado PENDIENTE o EN_REVISION
 * 2. Valida que el animal no tenga ya otra adopción
 * 3. Cambia la solicitud a APROBADA
 * 4. Cambia el animal a RESERVADO
 * 5. Cambia el adoptante a APROBADO
 * 6. Crea la Adopcion vinculada en estado PENDIENTE_FIRMA
 * 7. Envía notificaciones al adoptante y al admin
 */

@RequiredArgsConstructor
public class AprobarSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AnimalRepository animalRepository;
    private final AdoptanteRepository adoptanteRepository;
    private final AdopcionRepository adopcionRepository;
    private final NotificacionService notificacionService;

    @Transactional
    public Adopcion aprobar(SolicitudAdopcionId solicitudId) {
        // 1 — Buscar y validar la solicitud
        SolicitudAdopcion solicitud = solicitudAdopcionRepository.getById(solicitudId)
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(solicitudId.getValue()));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE
                && solicitud.getEstado() != EstadoSolicitud.EN_REVISION) {
            throw new SolicitudAdopcionEstadoInvalidoException(solicitud.getEstado().name());
        }

        // VALIDACIÓN: ¿El animal ya tiene una adopción?
        if (adopcionRepository.existsByAnimalId(solicitud.getAnimalId())) {
            throw new AnimalYaAdoptadoException(solicitud.getAnimalId().getValue());
        }

        // 2 — Actualizar solicitud → APROBADA
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitudAdopcionRepository.save(solicitud);

        // 3 — Actualizar animal → RESERVADO
        String animalNombre = animalRepository.getById(solicitud.getAnimalId())
                .map(animal -> {
                    animal.setEstado(EstadoAnimal.RESERVADO);
                    animalRepository.save(animal);
                    return animal.getNombre();
                })
                .orElse("el animal");

        // 4 — Actualizar adoptante → APROBADO e informar
        adoptanteRepository.getById(solicitud.getAdoptanteId())
                .ifPresent(adoptante -> {
                    adoptante.setEstadoValidacion(EstadoValidacion.APROBADO);
                    adoptanteRepository.save(adoptante);

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

        return adopcionRepository.save(nuevaAdopcion);
    }
}
