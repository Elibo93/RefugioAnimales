package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.usecase.voluntario.CreateVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Create Voluntario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class CreateVoluntarioService {

    private final CreateVoluntarioUseCase useCase;

    public Voluntario createVoluntario(CreateVoluntarioCommand command) {
        return useCase.create(command);
    }
}
