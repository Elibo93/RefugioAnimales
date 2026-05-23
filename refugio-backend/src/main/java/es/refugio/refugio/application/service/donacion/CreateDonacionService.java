package es.refugio.refugio.application.service.donacion;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.application.usecase.donacion.CreateDonacionUseCase;
import es.refugio.refugio.domain.model.donacion.Donacion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Create Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class CreateDonacionService {

    private final CreateDonacionUseCase useCase;

    public Donacion create(CreateDonacionCommand command) {
        return useCase.create(command);
    }
}
