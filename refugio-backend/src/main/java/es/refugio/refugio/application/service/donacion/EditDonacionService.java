package es.refugio.refugio.application.service.donacion;

import es.refugio.refugio.application.command.donacion.EditDonacionCommand;
import es.refugio.refugio.application.usecase.donacion.EditDonacionUseCase;
import es.refugio.refugio.domain.model.donacion.Donacion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Edit Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class EditDonacionService {

    private final EditDonacionUseCase useCase;

    public Donacion update(EditDonacionCommand command) {
        return useCase.update(command);
    }
}
