package es.refugio.refugio.application.service.donacion;

import es.refugio.refugio.application.usecase.donacion.DeleteDonacionUseCase;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteDonacionService {

    private final DeleteDonacionUseCase useCase;

    public void delete(DonacionId id) {
        useCase.delete(id);
    }
}
