package es.refugio.animales.refugio.domain.repository;

import es.refugio.animales.common.domain.repository.CRUDRepository;
import es.refugio.animales.refugio.domain.model.adoptante.Adoptante;
import es.refugio.animales.refugio.domain.model.adoptante.AdoptanteId;


public interface AdoptanteRepository extends CRUDRepository<Adoptante,AdoptanteId> {

}
