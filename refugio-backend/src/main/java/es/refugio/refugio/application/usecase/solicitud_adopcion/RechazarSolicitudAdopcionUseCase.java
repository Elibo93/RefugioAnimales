package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.error.SolicitudAdopcionEstadoInvalidoException;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso orquestador: Rechaza una solicitud de adopción.
 *
 * Pasos que realiza en una única transacción:
 * 1. Busca y valida que la solicitud está en estado PENDIENTE
 * 2. Actualiza la solicitud → RECHAZADA (con comentario opcional)
 * 3. Actualiza el adoptante → RECHAZADO
 */
@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Rechazar Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class RechazarSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AdoptanteRepository adoptanteRepository;
    private final AnimalRepository animalRepository;
    private final NotificacionService notificacionService;

    @Transactional
    public SolicitudAdopcion rechazar(SolicitudAdopcionId solicitudId, String comentario) {

        // 1 — Buscar y validar la solicitud
        SolicitudAdopcion solicitud = solicitudAdopcionRepository.getById(solicitudId)
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(solicitudId.getValue()));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE
                && solicitud.getEstado() != EstadoSolicitud.EN_REVISION) {
            throw new SolicitudAdopcionEstadoInvalidoException(solicitud.getEstado().name());
        }

        // 2 — Actualizar solicitud → RECHAZADA
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        if (comentario != null && !comentario.isBlank()) {
            solicitud.setComentario(comentario);
        }
        SolicitudAdopcion solicitudActualizada = solicitudAdopcionRepository.save(solicitud);

        // Obtener nombre del animal para la notificación
        String animalNombre = animalRepository.getById(solicitud.getAnimalId())
                .map(Animal::getNombre)
                .orElse("el animal");

        // 3 — Actualizar adoptante → RECHAZADO e informar
        adoptanteRepository.getById(solicitud.getAdoptanteId())
                .ifPresent(adoptante -> {
                    adoptante.setEstadoValidacion(EstadoValidacion.RECHAZADO);
                    adoptanteRepository.save(adoptante);

                    if (adoptante.getUsuarioId() != null) {
                        notificacionService.enviar(
                                adoptante.getUsuarioId(),
                                "Solicitud de Adopción Rechazada",
                                "Lo sentimos, tu solicitud para adoptar a " + animalNombre + " ha sido rechazada.",
                                "ADOPCION",
                                "/web/solicitudes/" + solicitud.getId().getValue() + "/detalle");
                    }
                });

        return solicitudActualizada;
    }
}
