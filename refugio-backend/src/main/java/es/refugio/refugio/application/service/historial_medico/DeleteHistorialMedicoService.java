package es.refugio.refugio.application.service.historial_medico;

import es.refugio.refugio.application.usecase.historial_medico.DeleteHistorialMedicoUseCase;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteHistorialMedicoService {

    private final DeleteHistorialMedicoUseCase useCase;

    public void delete(HistorialMedicoId id) {
        useCase.delete(id);
    }
}
