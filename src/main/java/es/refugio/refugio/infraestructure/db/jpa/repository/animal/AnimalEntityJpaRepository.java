package es.refugio.refugio.infraestructure.db.jpa.repository.animal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;

@Repository
public interface AnimalEntityJpaRepository extends JpaRepository<AnimalEntity, Integer>, JpaSpecificationExecutor<AnimalEntity> {

    Optional<AnimalEntity> findByNombre(String nombre);

    Optional<AnimalEntity> findByChipId(String chipId);

    List<AnimalEntity> findByEstado(EstadoAnimal estado);

    List<AnimalEntity> findByEspecie(Especie especie);

    List<AnimalEntity> findTop3ByEstadoOrderByVisitasDesc(EstadoAnimal estado);

    @Modifying
    @Transactional
    @Query("UPDATE AnimalEntity a SET a.visitas = a.visitas + 1 WHERE a.id = :id")
    void incrementarVisitas(@Param("id") Integer id);
}