package es.refugio.refugio.application.service.tarea;

import java.util.List;
import es.refugio.refugio.application.usecase.tarea.FindTareaUseCase;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindTareaService {

    private final FindTareaUseCase useCase;

    public List<Tarea> findAll() {
        return useCase.findAll();
    }

    public Tarea findById(TareaId id) {
        return useCase.findById(id);
    }
}
