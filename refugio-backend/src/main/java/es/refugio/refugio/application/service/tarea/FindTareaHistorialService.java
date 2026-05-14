package es.refugio.refugio.application.service.tarea;

import java.util.List;
import es.refugio.refugio.application.usecase.tarea.FindTareaHistorialUseCase;
import es.refugio.refugio.domain.model.tarea.TareaHistorial;
import es.refugio.refugio.domain.model.tarea.TareaId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindTareaHistorialService {

    private final FindTareaHistorialUseCase useCase;

    public List<TareaHistorial> findByTareaId(TareaId tareaId) {
        return useCase.findByTareaId(tareaId);
    }
}
