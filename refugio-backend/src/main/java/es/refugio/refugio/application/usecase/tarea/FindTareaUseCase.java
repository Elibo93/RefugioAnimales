package es.refugio.refugio.application.usecase.tarea;

import java.util.List;
import es.refugio.refugio.domain.error.TareaNotFoundException;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Find Tarea.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FindTareaUseCase {

    private final TareaRepository tareaRepository;

    public List<Tarea> findAll() {
        List<Tarea> tareas = tareaRepository.getAll();
        if (tareas.isEmpty()) {
            throw new TareaNotFoundException();
        }
        // Ordenar por fechaLimite de forma ascendente, colocando los nulos al final
        tareas.sort((t1, t2) -> {
            if (t1.getFechaLimite() == null && t2.getFechaLimite() == null) return 0;
            if (t1.getFechaLimite() == null) return 1;
            if (t2.getFechaLimite() == null) return -1;
            return t1.getFechaLimite().compareTo(t2.getFechaLimite());
        });
        return tareas;
    }

    public Page<Tarea> findAll(Pageable pageable) {
        return tareaRepository.findAll(pageable);
    }

    public Tarea findById(TareaId id) {
        return tareaRepository.getById(id)
                .orElseThrow(() -> new TareaNotFoundException(id.getValue()));
    }
}
