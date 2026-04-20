package es.refugio.refugio.application.service.adoptante;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.command.adoptante.ApproveAdoptanteCommand;
import es.refugio.refugio.application.usecase.adoptante.ApproveAdoptanteUseCase;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación que delega la aprobación de adoptante al UseCase correspondiente.
 */
@Service
@RequiredArgsConstructor
public class ApproveAdoptanteService {

    private final ApproveAdoptanteUseCase approveAdoptanteUseCase;

    public Adoptante approve(ApproveAdoptanteCommand command) {
        return approveAdoptanteUseCase.approve(command);
    }
}
