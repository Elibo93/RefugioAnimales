package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.usecase.voluntario.CreateVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateVoluntarioService {

    private final CreateVoluntarioUseCase useCase;

    public Voluntario createVoluntario(CreateVoluntarioCommand command) {
        return useCase.create(command);
    }
}
