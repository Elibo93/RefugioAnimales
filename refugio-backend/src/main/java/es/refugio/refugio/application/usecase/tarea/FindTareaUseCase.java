package es.refugio.refugio.application.usecase.tarea;

import java.util.List;
import es.refugio.refugio.domain.error.TareaNotFoundException;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindTareaUseCase {

    private final TareaRepository tareaRepository;

    public List<Tarea> findAll() {
        List<Tarea> tareas = tareaRepository.getAll();
        if (tareas.isEmpty()) {
            throw new TareaNotFoundException();
        }
        return tareas;
    }

    public Tarea findById(TareaId id) {
        return tareaRepository.getById(id)
                .orElseThrow(() -> new TareaNotFoundException(id.getValue()));
    }
}
