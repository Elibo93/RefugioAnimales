package es.refugio.refugio.application.usecase.tarea;

import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteTareaUseCase {

    private final TareaRepository tareaRepository;

    public void delete(TareaId id) {
        tareaRepository.deleteById(id);
    }
}
