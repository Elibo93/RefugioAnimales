package es.refugio.refugio.domain.repository;

import java.util.List;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;

public interface SolicitudAdopcionRepository extends CRUDRepository<SolicitudAdopcion, SolicitudAdopcionId> {

    List<SolicitudAdopcion> getByAnimalId(AnimalId animalId);
    
    List<SolicitudAdopcion> getByAdoptanteId(AdoptanteId adoptanteId);

}
