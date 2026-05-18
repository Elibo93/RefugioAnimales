package es.refugio.refugio.application.service.tarea;

import java.util.List;
import es.refugio.refugio.application.usecase.tarea.FindTareaUseCase;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class FindTareaService {

    private final FindTareaUseCase useCase;

    public List<Tarea> findAll() {
        return useCase.findAll();
    }

    public Page<Tarea> findAll(Pageable pageable) {
        return useCase.findAll(pageable);
    }

    public Tarea findById(TareaId id) {
        return useCase.findById(id);
    }
}
