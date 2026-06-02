package es.refugio.refugio.application.usecase.donacion;

import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Delete Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteDonacionUseCase {

    private final DonacionRepository donacionRepository;

    public void delete(DonacionId id) {
        donacionRepository.deleteById(id);
    }
}
