package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.domain.error.SolicitudAdopcionEstadoInvalidoException;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso orquestador: Rechaza una solicitud de adopción.
 *
 * Pasos que realiza en una única transacción:
 *  1. Busca y valida que la solicitud está en estado PENDIENTE
 *  2. Actualiza la solicitud → RECHAZADA (con comentario opcional)
 *  3. Actualiza el adoptante → RECHAZADO
 */
@RequiredArgsConstructor
public class RechazarSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AdoptanteRepository adoptanteRepository;

    @Transactional
    public SolicitudAdopcion rechazar(SolicitudAdopcionId solicitudId, String comentario) {

        // 1 — Buscar y validar la solicitud
        SolicitudAdopcion solicitud = solicitudAdopcionRepository.getById(solicitudId)
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(solicitudId.getValue()));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new SolicitudAdopcionEstadoInvalidoException(solicitud.getEstado().name());
        }

        // 2 — Actualizar solicitud → RECHAZADA
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        if (comentario != null && !comentario.isBlank()) {
            solicitud.setComentario(comentario);
        }
        SolicitudAdopcion solicitudActualizada = solicitudAdopcionRepository.save(solicitud);

        // 3 — Actualizar adoptante → RECHAZADO
        adoptanteRepository.getById(solicitud.getAdoptanteId())
                .map(adoptante -> {
                    adoptante.setEstadoValidacion(EstadoValidacion.RECHAZADO);
                    return adoptanteRepository.save(adoptante);
                });

        return solicitudActualizada;
    }
}
