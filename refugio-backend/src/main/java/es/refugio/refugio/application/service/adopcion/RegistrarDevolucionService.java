package es.refugio.refugio.application.service.adopcion;

import es.refugio.refugio.application.usecase.adopcion.RegistrarDevolucionUseCase;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrarDevolucionService {

    private final RegistrarDevolucionUseCase useCase;

    public Adopcion registrarDevolucion(Integer adopcionId) {
        return useCase.registrarDevolucion(adopcionId);
    }
}
