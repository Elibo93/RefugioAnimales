package es.refugio.refugio.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdopcionEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.SolicitudAdopcionEntity;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionResponse;

@Mapper(componentModel = "spring")
public interface AdopcionMapper {

    @Mapping(target = "estado", expression = "java(req.estado() != null ? es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion.valueOf(req.estado()) : null)")
    CreateAdopcionCommand toCommand(AdopcionRequest req);

    default EditAdopcionCommand toCommand(int id, AdopcionRequest req) {
        if (req == null) {
            return null;
        }
        return new EditAdopcionCommand(
                new AdopcionId(id),
                req.animalId(),
                req.adoptanteId(),
                req.fechaAdopcion(),
                req.estado(),
                req.contrato());
    }

    @Mapping(target = "id", source = "id", qualifiedByName = "adopcionIdToInteger")
    @Mapping(target = "animalId", source = "animalId", qualifiedByName = "animalIdToInteger")
    @Mapping(target = "adoptanteId", source = "adoptanteId", qualifiedByName = "adoptanteIdToInteger")
    @Mapping(target = "solicitudAdopcionId", source = "solicitudAdopcionId")
    @Mapping(target = "estado", expression = "java(a.getEstado() != null ? a.getEstado().name() : null)")
    AdopcionResponse toResponse(Adopcion a);

    List<AdopcionResponse> toResponse(List<Adopcion> lista);

    @Mapping(target = "id", source = "id", qualifiedByName = "adopcionIdToInteger")
    @Mapping(target = "animal", source = "animalId", qualifiedByName = "animalIdToEntity")
    @Mapping(target = "adoptante", source = "adoptanteId", qualifiedByName = "adoptanteIdToEntity")
    @Mapping(target = "solicitudAdopcion", source = "solicitudAdopcionId", qualifiedByName = "solicitudIdToEntity")
    AdopcionEntity toEntity(Adopcion a);

    @Mapping(target = "id", source = "id", qualifiedByName = "integerToAdopcionId")
    @Mapping(target = "animalId", source = "animal.id", qualifiedByName = "integerToAnimalId")
    @Mapping(target = "adoptanteId", source = "adoptante.id", qualifiedByName = "integerToAdoptanteId")
    @Mapping(target = "solicitudAdopcionId", source = "solicitudAdopcion.id")
    Adopcion toDomain(AdopcionEntity e);

    List<Adopcion> toDomain(List<AdopcionEntity> lista);

    // Named mappings for ID wrapping/unwrapping
    @Named("adopcionIdToInteger")
    default Integer adopcionIdToInteger(AdopcionId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("integerToAdopcionId")
    default AdopcionId integerToAdopcionId(Integer id) {
        return id != null ? new AdopcionId(id) : null;
    }

    @Named("animalIdToInteger")
    default Integer animalIdToInteger(AnimalId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("integerToAnimalId")
    default AnimalId integerToAnimalId(Integer id) {
        return id != null ? new AnimalId(id) : null;
    }

    @Named("adoptanteIdToInteger")
    default Integer adoptanteIdToInteger(AdoptanteId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("integerToAdoptanteId")
    default AdoptanteId integerToAdoptanteId(Integer id) {
        return id != null ? new AdoptanteId(id) : null;
    }

    @Named("animalIdToEntity")
    default AnimalEntity animalIdToEntity(AnimalId id) {
        if (id == null || id.getValue() == null)
            return null;
        return AnimalEntity.builder().id(id.getValue()).build();
    }

    @Named("adoptanteIdToEntity")
    default AdoptanteEntity adoptanteIdToEntity(AdoptanteId id) {
        if (id == null || id.getValue() == null)
            return null;
        return AdoptanteEntity.builder().id(id.getValue()).build();
    }

    @Named("solicitudIdToEntity")
    default SolicitudAdopcionEntity solicitudIdToEntity(Integer id) {
        if (id == null)
            return null;
        return SolicitudAdopcionEntity.builder().id(id).build();
    }
}
