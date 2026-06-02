package es.refugio.refugio.infraestructure.db.jpa.repository.gamificacion;

import es.refugio.refugio.domain.model.gamificacion.Logro;
import es.refugio.refugio.domain.model.gamificacion.enums.CategoriaLogro;
import es.refugio.refugio.domain.repository.gamificacion.LogroRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion.LogroEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class LogroJpaRepositoryImpl implements LogroRepository {
    private final JpaLogroRepository jpaRepository;

    public LogroJpaRepositoryImpl(JpaLogroRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Logro> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Logro> findByCategoria(CategoriaLogro categoria) {
        // Es necesario añadir el método a JpaRepository o filtrar aquí
        return jpaRepository.findAll().stream()
                .filter(e -> e.getCategoria() == categoria)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Logro> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private Logro toDomain(LogroEntity entity) {
        return new Logro(entity.getId(), entity.getCodigo(), entity.getNombre(), entity.getDescripcion(),
                entity.getCategoria(), entity.getRequisitoTipo(), entity.getRequisitoValor(), 
                entity.getIconoLucide(), entity.getRareza());
    }
}
