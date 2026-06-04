package es.refugio.refugio.application.usecase.tarea;

import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Delete Tarea.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteTareaUseCase {

    private final TareaRepository tareaRepository;

    public void delete(TareaId id) {
        tareaRepository.deleteById(id);
    }
}
