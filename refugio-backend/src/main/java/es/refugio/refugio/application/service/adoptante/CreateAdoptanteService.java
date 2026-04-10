package es.refugio.refugio.application.service.adoptante;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.application.usecase.adoptante.CreateAdoptanteUseCase;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreateAdoptanteService {

    private final CreateAdoptanteUseCase createAdoptanteUseCase;

    public Adoptante createAdoptante(CreateAdoptanteCommand command) {
        // Delegamos la lógica de creación al caso de uso
        return createAdoptanteUseCase.create(command);
    }
}