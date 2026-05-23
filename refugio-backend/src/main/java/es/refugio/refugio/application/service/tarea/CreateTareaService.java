package es.refugio.refugio.application.service.tarea;

import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.application.usecase.tarea.CreateTareaUseCase;
import es.refugio.refugio.domain.model.tarea.Tarea;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Create Tarea.
 *
 * @author Elisabeth
 * @author Diego
 */
public class CreateTareaService {

    private final CreateTareaUseCase useCase;

    public Tarea create(CreateTareaCommand command) {
        return useCase.create(command);
    }
}
