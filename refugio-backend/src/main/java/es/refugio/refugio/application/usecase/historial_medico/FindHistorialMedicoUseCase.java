package es.refugio.refugio.application.usecase.historial_medico;

import java.util.List;
import es.refugio.refugio.domain.error.HistorialMedicoNotFoundException;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.domain.repository.HistorialMedicoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindHistorialMedicoUseCase {

    private final HistorialMedicoRepository historialMedicoRepository;

    public List<HistorialMedico> findAll() {
        List<HistorialMedico> historiales = historialMedicoRepository.getAll();
        if (historiales.isEmpty()) {
            throw new HistorialMedicoNotFoundException();
        }
        return historiales;
    }

    public HistorialMedico findById(HistorialMedicoId id) {
        return historialMedicoRepository.getById(id)
                .orElseThrow(() -> new HistorialMedicoNotFoundException(id.getValue()));
    }

    public List<HistorialMedico> findByAnimalId(AnimalId animalId) {
        return historialMedicoRepository.getByAnimalId(animalId);
    }
}
