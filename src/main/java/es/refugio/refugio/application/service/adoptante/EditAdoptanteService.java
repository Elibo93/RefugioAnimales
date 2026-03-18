package es.refugio.refugio.application.service.adoptante;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.command.adoptante.EditAdoptanteCommand;
import es.refugio.refugio.application.usecase.adoptante.EditAdoptanteUseCase;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditAdoptanteService {

    private final EditAdoptanteUseCase editAdoptanteUseCase;

    public Adoptante update(EditAdoptanteCommand command) {
        // Delegamos la actualización de los datos al caso de uso
        return editAdoptanteUseCase.update(command);
    }
}