package es.refugio.refugio.infraestructure.db.jpa.repository.adoptante;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.mapper.AdoptanteMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdoptanteJpaRepositoryImpl implements AdoptanteRepository {

    private final AdoptanteEntityJpaRepository repository;

    @Override
    public Adoptante save(Adoptante t) {
        AdoptanteEntity adoptanteEntity = AdoptanteMapper.toEntity(t);
        return AdoptanteMapper.toDomain(repository.save(adoptanteEntity));
    }

    @Override
    public List<Adoptante> getAll() {
        return AdoptanteMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Adoptante> getById(AdoptanteId id) {
        return repository.findById(id.getValue())
                .map(AdoptanteMapper::toDomain);
    }

    @Override
    public void deleteById(AdoptanteId id) {
        repository.deleteById(id.getValue());
    }


    @Override
    public Optional<Adoptante> getByUsuarioId(UsuarioId usuarioId) {
        return repository.findByUsuarioId(usuarioId.getValue())
                .map(AdoptanteMapper::toDomain);
    }
}