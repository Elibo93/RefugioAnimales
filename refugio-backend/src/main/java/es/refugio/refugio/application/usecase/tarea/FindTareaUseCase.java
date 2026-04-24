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
        // Sort by fechaLimite ascending, putting nulls at the end
        tareas.sort((t1, t2) -> {
            if (t1.getFechaLimite() == null && t2.getFechaLimite() == null) return 0;
            if (t1.getFechaLimite() == null) return 1;
            if (t2.getFechaLimite() == null) return -1;
            return t1.getFechaLimite().compareTo(t2.getFechaLimite());
        });
        return tareas;
    }

    public Tarea findById(TareaId id) {
        return tareaRepository.getById(id)
                .orElseThrow(() -> new TareaNotFoundException(id.getValue()));
    }
}
