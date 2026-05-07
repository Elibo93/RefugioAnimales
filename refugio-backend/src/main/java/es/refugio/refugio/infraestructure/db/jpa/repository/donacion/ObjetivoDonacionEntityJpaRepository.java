package es.refugio.refugio.infraestructure.db.jpa.repository.donacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.refugio.refugio.infraestructure.db.jpa.entity.ObjetivoDonacionEntity;

@Repository
public interface ObjetivoDonacionEntityJpaRepository extends JpaRepository<ObjetivoDonacionEntity, Integer> {
}
