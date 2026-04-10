package es.refugio.refugio.application.usecase.historial_medico;

import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.domain.repository.HistorialMedicoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteHistorialMedicoUseCase {

    private final HistorialMedicoRepository historialMedicoRepository;

    public void delete(HistorialMedicoId id) {
        historialMedicoRepository.deleteById(id);
    }
}
