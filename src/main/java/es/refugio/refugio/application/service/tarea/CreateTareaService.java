package es.refugio.refugio.application.service.tarea;

import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.application.usecase.tarea.CreateTareaUseCase;
import es.refugio.refugio.domain.model.tarea.Tarea;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTareaService {

    private final CreateTareaUseCase useCase;

    public Tarea create(CreateTareaCommand command) {
        return useCase.create(command);
    }
}
