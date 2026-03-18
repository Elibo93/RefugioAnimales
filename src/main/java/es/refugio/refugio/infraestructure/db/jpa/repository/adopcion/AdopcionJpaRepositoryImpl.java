package es.refugio.refugio.infraestructure.db.jpa.repository.adopcion;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdopcionEntity;
import es.refugio.refugio.infraestructure.mapper.AdopcionMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdopcionJpaRepositoryImpl implements AdopcionRepository {

    private final AdopcionEntityJpaRepository jpaRepository;

    @Override
    public Adopcion save(Adopcion adopcion) {
        AdopcionEntity entity = AdopcionMapper.toEntity(adopcion);
        return AdopcionMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Adopcion> getById(AdopcionId id) {
        return jpaRepository.findById(id.getValue())
                .map(AdopcionMapper::toDomain);
    }

    @Override
    public List<Adopcion> getAll() {
        return AdopcionMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public void deleteById(AdopcionId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<Adopcion> getByAnimalId(AnimalId animalId) {
        return AdopcionMapper.toDomain(jpaRepository.findByAnimalId(animalId.getValue()));
    }

    @Override
    public List<Adopcion> getByAdoptanteId(AdoptanteId adoptanteId) {
        return AdopcionMapper.toDomain(jpaRepository.findByAdoptanteId(adoptanteId.getValue()));
    }

    @Override
    public Optional<Adopcion> getByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId) {
        return jpaRepository.findByAdoptanteIdAndAnimalId(adoptanteId.getValue(), animalId.getValue())
                .map(AdopcionMapper::toDomain);
    }

    @Override
    public boolean existsByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId) {
        return jpaRepository.existsByAdoptanteIdAndAnimalId(adoptanteId.getValue(), animalId.getValue());
    }
}