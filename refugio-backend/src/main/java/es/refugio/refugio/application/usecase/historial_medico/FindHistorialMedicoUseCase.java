package es.refugio.refugio.application.usecase.historial_medico;

import java.util.List;
import es.refugio.refugio.domain.error.HistorialMedicoNotFoundException;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.domain.repository.HistorialMedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Find Historial Medico.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FindHistorialMedicoUseCase {

    private final HistorialMedicoRepository historialMedicoRepository;

    public List<HistorialMedico> findAll() {
        List<HistorialMedico> historiales = historialMedicoRepository.getAll();
        if (historiales.isEmpty()) {
            throw new HistorialMedicoNotFoundException();
        }
        return historiales;
    }

    public Page<HistorialMedico> findAll(Pageable pageable) {
        return historialMedicoRepository.findAll(pageable);
    }

    public HistorialMedico findById(HistorialMedicoId id) {
        return historialMedicoRepository.getById(id)
                .orElseThrow(() -> new HistorialMedicoNotFoundException(id.getValue()));
    }

    public List<HistorialMedico> findByAnimalId(AnimalId animalId) {
        return historialMedicoRepository.getByAnimalId(animalId);
    }
}
