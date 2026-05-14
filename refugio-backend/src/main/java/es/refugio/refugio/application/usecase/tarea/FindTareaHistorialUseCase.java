package es.refugio.refugio.application.usecase.tarea;

import java.util.List;
import es.refugio.refugio.domain.model.tarea.TareaHistorial;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaHistorialRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindTareaHistorialUseCase {

    private final TareaHistorialRepository repository;

    public List<TareaHistorial> findByTareaId(TareaId tareaId) {
        return repository.findByTareaId(tareaId);
    }
}
