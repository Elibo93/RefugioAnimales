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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class HistorialMedicoJpaRepositoryImpl implements HistorialMedicoRepository {

    private final HistorialMedicoEntityJpaRepository jpaRepository;
    private final HistorialMedicoMapper historialMedicoMapper;

    @Override
    public HistorialMedico save(HistorialMedico historialMedico) {
        HistorialMedicoEntity entity = historialMedicoMapper.toEntity(historialMedico);
        HistorialMedicoEntity savedEntity = jpaRepository.save(entity);
        return historialMedicoMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<HistorialMedico> getById(HistorialMedicoId id) {
        return jpaRepository.findById(id.getValue())
                .map(historialMedicoMapper::toDomain);
    }

    @Override
    public List<HistorialMedico> getAll() {
        return historialMedicoMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public Page<HistorialMedico> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(historialMedicoMapper::toDomain);
    }

    @Override
    public void deleteById(HistorialMedicoId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<HistorialMedico> getByAnimalId(AnimalId animalId) {
        List<HistorialMedicoEntity> entities = jpaRepository.findByAnimalId(animalId.getValue());
        return historialMedicoMapper.toDomain(entities);
    }

}
