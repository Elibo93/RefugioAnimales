package es.refugio.refugio.application.service.tarea;

import es.refugio.refugio.application.usecase.tarea.DeleteTareaUseCase;
import es.refugio.refugio.domain.model.tarea.TareaId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteTareaService {

    private final DeleteTareaUseCase useCase;

    public void delete(TareaId id) {
        useCase.delete(id);
    }
}
