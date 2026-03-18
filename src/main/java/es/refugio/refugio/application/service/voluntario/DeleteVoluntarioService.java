package es.refugio.refugio.application.service.voluntario;

import es.refugio.refugio.application.usecase.voluntario.DeleteVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteVoluntarioService {

    // Atributos
    private final DeleteVoluntarioUseCase deleteVoluntarioUseCase;

    public void delete(VoluntarioId id) {
        deleteVoluntarioUseCase.delete(id);
    }

}

















