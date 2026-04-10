package es.refugio.refugio.application.service.tarea;

import es.refugio.refugio.application.command.tarea.EditTareaCommand;
import es.refugio.refugio.application.usecase.tarea.EditTareaUseCase;
import es.refugio.refugio.domain.model.tarea.Tarea;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditTareaService {

    private final EditTareaUseCase useCase;

    public Tarea update(EditTareaCommand command) {
        return useCase.update(command);
    }
}
