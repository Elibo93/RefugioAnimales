package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.application.service.voluntario.EditVoluntarioService;
import es.refugio.refugio.application.usecase.voluntario.EditVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditVoluntarioService {

    private final EditVoluntarioUseCase useCase;

    public Voluntario update(EditVoluntarioCommand command) {
        return useCase.update(command);
    }
}
