package es.refugio.refugio.application.service.animal;

import org.springframework.stereotype.Service;
import es.refugio.refugio.application.usecase.animal.IncrementarVisitasUseCase;
import es.refugio.refugio.domain.model.animal.AnimalId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class IncrementarVisitasService {

    private final IncrementarVisitasUseCase incrementarVisitasUseCase;

    public void incrementar(AnimalId id) {
        incrementarVisitasUseCase.execute(id);
    }
}
