package es.refugio.refugio.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import es.refugio.refugio.application.command.historial_medico.CreateHistorialMedicoCommand;
import es.refugio.refugio.application.command.historial_medico.EditHistorialMedicoCommand;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.HistorialMedicoEntity;
import es.refugio.refugio.infraestructure.web.dto.historial_medico.HistorialMedicoRequest;
import es.refugio.refugio.infraestructure.web.dto.historial_medico.HistorialMedicoResponse;

@Mapper(componentModel = "spring")
public interface HistorialMedicoMapper {

    CreateHistorialMedicoCommand toCommand(HistorialMedicoRequest req);

    default EditHistorialMedicoCommand toCommand(int id, HistorialMedicoRequest req) {
        if ( req == null ) {
            return null;
        }
        return new EditHistorialMedicoCommand(
            new HistorialMedicoId(id),
            req.animalId(),
            req.fecha(),
            req.descripcion(),
            req.tratamiento(),
            req.veterinario()
        );
    }

    @Mapping(target = "id", source = "id", qualifiedByName = "historialMedicoIdToInteger")
    @Mapping(target = "animalId", source = "animalId", qualifiedByName = "animalIdToInteger")
    HistorialMedicoResponse toResponse(HistorialMedico h);

    List<HistorialMedicoResponse> toResponse(List<HistorialMedico> lista);

    @Mapping(target = "id", source = "id", qualifiedByName = "historialMedicoIdToInteger")
    @Mapping(target = "animal", source = "animalId", qualifiedByName = "animalIdToEntity")
    HistorialMedicoEntity toEntity(HistorialMedico h);

    @Mapping(target = "id", source = "id", qualifiedByName = "integerToHistorialMedicoId")
    @Mapping(target = "animalId", source = "animal.id", qualifiedByName = "integerToAnimalId")
    HistorialMedico toDomain(HistorialMedicoEntity e);

    List<HistorialMedico> toDomain(List<HistorialMedicoEntity> lista);

    @Named("historialMedicoIdToInteger")
    default Integer historialMedicoIdToInteger(HistorialMedicoId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("integerToHistorialMedicoId")
    default HistorialMedicoId integerToHistorialMedicoId(Integer id) {
        return id != null ? new HistorialMedicoId(id) : null;
    }

    @Named("animalIdToInteger")
    default Integer animalIdToInteger(AnimalId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("integerToAnimalId")
    default AnimalId integerToAnimalId(Integer id) {
        return id != null ? new AnimalId(id) : null;
    }

    @Named("animalIdToEntity")
    default AnimalEntity animalIdToEntity(AnimalId id) {
        if (id == null || id.getValue() == null) return null;
        return AnimalEntity.builder().id(id.getValue()).build();
    }
}
