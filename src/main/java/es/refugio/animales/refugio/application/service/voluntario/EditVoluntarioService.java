package es.refugio.animales.refugio.application.service.voluntario;

import es.refugio.animales.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.animales.refugio.application.usecase.voluntario.EditVoluntarioUseCase;
import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
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

















