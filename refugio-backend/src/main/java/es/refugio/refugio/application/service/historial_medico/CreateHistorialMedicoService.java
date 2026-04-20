package es.refugio.refugio.application.service.historial_medico;

import es.refugio.refugio.application.command.historial_medico.CreateHistorialMedicoCommand;
import es.refugio.refugio.application.usecase.historial_medico.CreateHistorialMedicoUseCase;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateHistorialMedicoService {

    private final CreateHistorialMedicoUseCase useCase;

    public HistorialMedico create(CreateHistorialMedicoCommand command) {
        return useCase.create(command);
    }
}
