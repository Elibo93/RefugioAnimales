package es.refugio.refugio.application.service.donacion;

import es.refugio.refugio.application.usecase.donacion.DeleteDonacionUseCase;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Delete Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteDonacionService {

    private final DeleteDonacionUseCase useCase;

    public void delete(DonacionId id) {
        useCase.delete(id);
    }
}
