package es.refugio.animales.refugio.infraestructure.db.jpa.repository.persona;

import java.util.List;
import java.util.Optional;

import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.domain.model.usuario.PersonaId;
import es.refugio.animales.refugio.domain.repository.PersonaRepository;
import es.refugio.animales.refugio.infraestructure.db.jpa.entity.PersonaEntity;
import es.refugio.animales.refugio.infraestructure.mapper.PersonaMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonaJpaRepositoryImpl implements PersonaRepository {
    private final PersonaEntityJpaRepository repository;

    @Override
    public Persona save(Persona t) {

        PersonaEntity prod = PersonaMapper.toEntity(t);
        return PersonaMapper.toDomain(repository.save(prod));
    }

    @Override
    public List<Persona> getAll() {
        return PersonaMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Persona> getById(PersonaId id) {
        Optional<PersonaEntity> pe = repository.findById(id.getValue());

        if (pe.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(PersonaMapper.toDomain(pe.get()));
    }

    @Override
    public void deleteById(PersonaId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public Optional<Persona> getByName(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByName'");
    }
    // Hereda automáticamente métodos como: save(), findById(), findAll(), delete(),
    // etc.

}
