package es.refugio.refugio.domain.repository;

import java.util.List;
import es.refugio.refugio.domain.model.tarea.TareaHistorial;
import es.refugio.refugio.domain.model.tarea.TareaId;

public interface TareaHistorialRepository {
    TareaHistorial save(TareaHistorial historial);
    List<TareaHistorial> findByTareaId(TareaId tareaId);
}
