package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.usecase.voluntario.ApproveVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Approve Voluntario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class ApproveVoluntarioService {

    private final ApproveVoluntarioUseCase useCase;

    public void approve(int id, String adminToken) {
        useCase.approve(new VoluntarioId(id), adminToken);
    }

    public void reject(int id, String adminToken) {
        useCase.reject(new VoluntarioId(id), adminToken);
    }
}
