package es.refugio.refugio.domain.repository;

import java.util.List;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.domain.model.animal.AnimalId;

public interface HistorialMedicoRepository extends CRUDRepository<HistorialMedico, HistorialMedicoId> {

    List<HistorialMedico> getByAnimalId(AnimalId animalId);

}
