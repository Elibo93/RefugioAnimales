package es.refugio.refugio.application.service.adopcion;

import es.refugio.refugio.application.usecase.adopcion.StartPeriodoAdaptacionUseCase;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartPeriodoAdaptacionService {

    private final StartPeriodoAdaptacionUseCase useCase;

    public Adopcion startPeriodoAdaptacion(Integer adopcionId) {
        return useCase.startPeriodoAdaptacion(adopcionId);
    }
}
