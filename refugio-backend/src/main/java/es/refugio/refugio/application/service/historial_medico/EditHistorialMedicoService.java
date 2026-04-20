package es.refugio.refugio.application.service.historial_medico;

import es.refugio.refugio.application.command.historial_medico.EditHistorialMedicoCommand;
import es.refugio.refugio.application.usecase.historial_medico.EditHistorialMedicoUseCase;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditHistorialMedicoService {

    private final EditHistorialMedicoUseCase useCase;

    public HistorialMedico update(EditHistorialMedicoCommand command) {
        return useCase.update(command);
    }
}
