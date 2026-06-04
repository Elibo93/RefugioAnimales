package es.refugio.refugio.application.service.adopcion;

import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.application.usecase.adopcion.EditAdopcionUseCase;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Edit Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class EditAdopcionService {

    private final EditAdopcionUseCase useCase;

    public Adopcion update(EditAdopcionCommand command) {
        return useCase.update(command);
    }
}
