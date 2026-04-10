package es.refugio.refugio.application.service.adoptante;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.command.adoptante.RejectAdoptanteCommand;
import es.refugio.refugio.application.usecase.adoptante.RejectAdoptanteUseCase;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación que delega el rechazo de adoptante al UseCase correspondiente.
 */
@Service
@RequiredArgsConstructor
public class RejectAdoptanteService {

    private final RejectAdoptanteUseCase rejectAdoptanteUseCase;

    public Adoptante reject(RejectAdoptanteCommand command) {
        return rejectAdoptanteUseCase.reject(command);
    }
}
