package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;

    public void delete(SolicitudAdopcionId id) {
        solicitudAdopcionRepository.deleteById(id);
    }
}
