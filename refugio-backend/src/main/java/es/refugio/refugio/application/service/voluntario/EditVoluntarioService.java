package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.application.service.voluntario.EditVoluntarioService;
import es.refugio.refugio.application.usecase.voluntario.EditVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Edit Voluntario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class EditVoluntarioService {

    private final EditVoluntarioUseCase useCase;

    public Voluntario update(EditVoluntarioCommand command) {
        return useCase.update(command);
    }
}
