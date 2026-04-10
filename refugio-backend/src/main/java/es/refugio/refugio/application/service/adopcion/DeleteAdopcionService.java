package es.refugio.refugio.application.service.adopcion;

import es.refugio.refugio.application.usecase.adopcion.DeleteAdopcionUseCase;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteAdopcionService {

    private final DeleteAdopcionUseCase useCase;

    public void delete(AdopcionId id) {
        useCase.delete(id);
    }
}
