package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.usecase.voluntario.DeleteVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteVoluntarioService {

    private final DeleteVoluntarioUseCase useCase;

    public void delete(VoluntarioId id) {
        useCase.delete(id);
    }
}
