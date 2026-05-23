package es.refugio.refugio.application.usecase.historial_medico;

import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.domain.repository.HistorialMedicoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Delete Historial Medico.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteHistorialMedicoUseCase {

    private final HistorialMedicoRepository historialMedicoRepository;

    public void delete(HistorialMedicoId id) {
        historialMedicoRepository.deleteById(id);
    }
}
