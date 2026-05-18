package es.refugio.refugio.domain.repository;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TareaRepository extends CRUDRepository<Tarea, TareaId> {
    default Page<Tarea> findAll(Pageable pageable) {
        return Page.empty();
    }
}
