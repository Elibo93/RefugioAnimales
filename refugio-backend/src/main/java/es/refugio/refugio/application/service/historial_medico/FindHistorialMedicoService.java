package es.refugio.refugio.application.service.historial_medico;

import java.util.List;
import es.refugio.refugio.application.usecase.historial_medico.FindHistorialMedicoUseCase;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindHistorialMedicoService {

    private final FindHistorialMedicoUseCase useCase;

    public List<HistorialMedico> findAll() {
        return useCase.findAll();
    }

    public HistorialMedico findById(HistorialMedicoId id) {
        return useCase.findById(id);
    }

    public List<HistorialMedico> findByAnimalId(AnimalId animalId) {
        return useCase.findByAnimalId(animalId);
    }
}
