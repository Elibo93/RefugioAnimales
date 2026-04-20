package es.refugio.refugio.application.service.adopcion;

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.application.usecase.adopcion.CreateAdopcionUseCase;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAdopcionService {

    private final CreateAdopcionUseCase useCase;

    public Adopcion createAdopcion(CreateAdopcionCommand command) {
        return useCase.create(command);
    }
}
