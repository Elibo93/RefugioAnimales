package es.refugio.refugio.infraestructure.db.jpa.repository.adoptante;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.mapper.AdoptanteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component // Importante para que Spring lo encuentre como implementación de la interfaz de dominio
public class AdoptanteJpaRepositoryImpl implements AdoptanteRepository {

    private final AdoptanteEntityJpaRepository repository;

    @Override
    public Adoptante save(Adoptante t) {
        // Transformamos de Dominio a Entidad JPA
        AdoptanteEntity adoptanteEntity = AdoptanteMapper.toEntity(t);
        // Guardamos y transformamos de vuelta a Dominio
        return AdoptanteMapper.toDomain(repository.save(adoptanteEntity));
    }

    @Override
    public List<Adoptante> getAll() {
        // Recuperamos todas las entidades y las mapeamos a la lista de dominio
        return AdoptanteMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Adoptante> getById(AdoptanteId id) {
        // Buscamos por el valor primitivo del ID tipado
        Optional<AdoptanteEntity> te = repository.findById(id.getValue());

        if (te.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(AdoptanteMapper.toDomain(te.get()));
    }

    @Override
    public void deleteById(AdoptanteId id) {
        repository.deleteById(id.getValue());
    }
}