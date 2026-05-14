package es.refugio.refugio.domain.repository.gamificacion;

import es.refugio.refugio.domain.model.gamificacion.Logro;
import es.refugio.refugio.domain.model.gamificacion.enums.CategoriaLogro;

import java.util.List;
import java.util.Optional;

public interface LogroRepository {
    List<Logro> findAll();
    List<Logro> findByCategoria(CategoriaLogro categoria);
    Optional<Logro> findById(Long id);
}
