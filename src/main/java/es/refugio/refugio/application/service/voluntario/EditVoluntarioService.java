package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.application.usecase.voluntario.EditVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditVoluntarioService {

    // Atributos
    private final EditVoluntarioUseCase editVoluntarioUseCase;

    public Voluntario update(EditVoluntarioCommand command) {
        Voluntario voluntario = editVoluntarioUseCase.update(command);
        return voluntario;
    }
}

















