package es.refugio.refugio.domain.repository;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;

public interface TareaRepository extends CRUDRepository<Tarea, TareaId> {
}
