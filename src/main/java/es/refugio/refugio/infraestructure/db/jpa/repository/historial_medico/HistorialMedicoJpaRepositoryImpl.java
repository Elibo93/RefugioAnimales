package es.refugio.refugio.infraestructure.db.jpa.repository.historial_medico;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.domain.repository.HistorialMedicoRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.HistorialMedicoEntity;
import es.refugio.refugio.infraestructure.mapper.HistorialMedicoMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HistorialMedicoJpaRepositoryImpl implements HistorialMedicoRepository {

    private final HistorialMedicoEntityJpaRepository jpaRepository;

    @Override
    public HistorialMedico save(HistorialMedico historialMedico) {
        HistorialMedicoEntity entity = HistorialMedicoMapper.toEntity(historialMedico);
        HistorialMedicoEntity savedEntity = jpaRepository.save(entity);
        return HistorialMedicoMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<HistorialMedico> getById(HistorialMedicoId id) {
        return jpaRepository.findById(id.getValue())
                .map(HistorialMedicoMapper::toDomain);
    }

    @Override
    public List<HistorialMedico> getAll() {
        return HistorialMedicoMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public void deleteById(HistorialMedicoId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<HistorialMedico> getByAnimalId(AnimalId animalId) {
        List<HistorialMedicoEntity> entities = jpaRepository.findByAnimalId(animalId.getValue());
        return HistorialMedicoMapper.toDomain(entities);
    }

}
