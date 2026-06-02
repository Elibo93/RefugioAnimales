package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.usecase.voluntario.DeleteVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Delete Voluntario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteVoluntarioService {

    private final DeleteVoluntarioUseCase useCase;

    public void delete(VoluntarioId id) {
        useCase.delete(id);
    }
}
