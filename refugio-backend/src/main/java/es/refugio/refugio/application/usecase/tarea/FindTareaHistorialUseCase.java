package es.refugio.refugio.application.usecase.tarea;

import java.util.List;
import es.refugio.refugio.domain.model.tarea.TareaHistorial;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaHistorialRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Find Tarea Historial.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FindTareaHistorialUseCase {

    private final TareaHistorialRepository repository;

    public List<TareaHistorial> findByTareaId(TareaId tareaId) {
        return repository.findByTareaId(tareaId);
    }
}
