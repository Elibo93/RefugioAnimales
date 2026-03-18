package es.refugio.refugio.application.service.adopcion;

import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.application.usecase.adopcion.EditAdopcionUseCase;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditAdopcionService {

    private final EditAdopcionUseCase useCase;

    public Adopcion update(EditAdopcionCommand command) {
        return useCase.update(command);
    }
}
