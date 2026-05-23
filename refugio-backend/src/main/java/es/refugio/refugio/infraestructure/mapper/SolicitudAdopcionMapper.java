package es.refugio.refugio.infraestructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.SolicitudAdopcionEntity;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionResponse;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionUpdateRequest;

@Mapper(componentModel = "spring")
public interface SolicitudAdopcionMapper {

    CreateSolicitudAdopcionCommand toCommand(SolicitudAdopcionRequest req);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapSolicitudAdopcionId")
    @Mapping(target = "comentarioAdmin", ignore = true)
    EditSolicitudAdopcionCommand toCommand(int id, SolicitudAdopcionRequest req);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapSolicitudAdopcionId")
    EditSolicitudAdopcionCommand toCommand(int id, SolicitudAdopcionUpdateRequest req);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "animalId", source = "animalId.value")
    @Mapping(target = "adoptanteId", source = "adoptanteId.value")
    SolicitudAdopcionResponse toResponse(SolicitudAdopcion s);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "animal", source = "animalId", qualifiedByName = "mapAnimalEntity")
    @Mapping(target = "adoptante", source = "adoptanteId", qualifiedByName = "mapAdoptanteEntity")
    SolicitudAdopcionEntity toEntity(SolicitudAdopcion s);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapSolicitudAdopcionId")
    @Mapping(target = "animalId", source = "animal.id", qualifiedByName = "mapAnimalId")
    @Mapping(target = "adoptanteId", source = "adoptante.id", qualifiedByName = "mapAdoptanteId")
    SolicitudAdopcion toDomain(SolicitudAdopcionEntity e);

    List<SolicitudAdopcion> toDomain(List<SolicitudAdopcionEntity> lista);

    List<SolicitudAdopcionResponse> toResponse(List<SolicitudAdopcion> lista);

    @Named("mapSolicitudAdopcionId")
    default SolicitudAdopcionId mapSolicitudAdopcionId(Integer id) {
        return id != null ? new SolicitudAdopcionId(id) : null;
    }

    @Named("mapAnimalId")
    default AnimalId mapAnimalId(Integer id) {
        return id != null ? new AnimalId(id) : null;
    }

    @Named("mapAdoptanteId")
    default AdoptanteId mapAdoptanteId(Integer id) {
        return id != null ? new AdoptanteId(id) : null;
    }

    @Named("mapAnimalEntity")
    default AnimalEntity mapAnimalEntity(AnimalId id) {
        return id != null ? AnimalEntity.builder().id(id.getValue()).build() : null;
    }

    @Named("mapAdoptanteEntity")
    default AdoptanteEntity mapAdoptanteEntity(AdoptanteId id) {
        return id != null ? AdoptanteEntity.builder().id(id.getValue()).build() : null;
    }
}
