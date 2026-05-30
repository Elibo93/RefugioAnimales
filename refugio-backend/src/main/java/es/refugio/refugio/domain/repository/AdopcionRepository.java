package es.refugio.refugio.domain.repository;

import java.util.List;
import java.util.Optional;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdopcionRepository extends CRUDRepository<Adopcion, AdopcionId> {

    List<Adopcion> getByAdoptanteId(AdoptanteId adoptanteId);

    List<Adopcion> getByAnimalId(AnimalId animalId);

    Optional<Adopcion> getByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId);

    boolean existsByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId);

    List<Adopcion> findByCriteria(AdoptanteId adoptanteId, AnimalId animalId);
    boolean existsByAnimalId(AnimalId animalId);

    List<Adopcion> findByEstadoAndFechaAdopcionBefore(es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion estado, java.time.LocalDateTime date);

    default Page<Adopcion> findAll(Pageable pageable) {
        return Page.empty();
    }

    Page<Adopcion> findFiltered(String q, String estado, Pageable pageable);
}