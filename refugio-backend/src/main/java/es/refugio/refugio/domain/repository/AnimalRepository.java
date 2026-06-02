package es.refugio.refugio.domain.repository;

import java.util.List;
import java.util.Optional;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnimalRepository extends CRUDRepository<Animal, AnimalId> {

    Optional<Animal> getByChipId(String chipId);

    List<Animal> getByEstado(EstadoAnimal estado);

    List<Animal> getByEspecie(Especie especie);

    List<Animal> findFiltered(String q, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia);
    
    default Page<Animal> findFiltered(String q, String estado, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia, Pageable pageable) {
        return Page.empty();
    }

    List<Animal> findTop3ByEstadoOrderByVisitasDesc(EstadoAnimal estado);

    void incrementarVisitas(AnimalId id);

    default Page<Animal> findAll(Pageable pageable) {
        return Page.empty();
    }
}
