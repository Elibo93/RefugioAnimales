package es.refugio.refugio.application.usecase.donacion;

import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteDonacionUseCase {

    private final DonacionRepository donacionRepository;

    public void delete(DonacionId id) {
        donacionRepository.deleteById(id);
    }
}
