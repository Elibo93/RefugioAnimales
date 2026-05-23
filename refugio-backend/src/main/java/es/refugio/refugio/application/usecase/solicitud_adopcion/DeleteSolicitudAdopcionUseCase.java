package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Delete Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;

    public void delete(SolicitudAdopcionId id) {
        solicitudAdopcionRepository.deleteById(id);
    }
}
