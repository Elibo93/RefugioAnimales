package es.refugio.refugio.application.service.historial_medico;

import java.util.List;
import es.refugio.refugio.application.usecase.historial_medico.FindHistorialMedicoUseCase;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Find Historial Medico.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FindHistorialMedicoService {

    private final FindHistorialMedicoUseCase useCase;

    public List<HistorialMedico> findAll() {
        return useCase.findAll();
    }

    public Page<HistorialMedico> findAll(Pageable pageable) {
        return useCase.findAll(pageable);
    }

    public HistorialMedico findById(HistorialMedicoId id) {
        return useCase.findById(id);
    }

    public List<HistorialMedico> findByAnimalId(AnimalId animalId) {
        return useCase.findByAnimalId(animalId);
    }
}
