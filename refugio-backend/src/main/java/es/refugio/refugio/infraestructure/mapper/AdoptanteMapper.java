package es.refugio.refugio.infraestructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.application.command.adoptante.EditAdoptanteCommand;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteRequest;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteResponse;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteUpdateRequest;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;

@Mapper(componentModel = "spring")
public interface AdoptanteMapper {

    CreateAdoptanteCommand toCommand(AdoptanteRequest request);

    @Mapping(target = "id", source = "id")
    EditAdoptanteCommand toEditCommand(AdoptanteId id, AdoptanteUpdateRequest request);

    @Mapping(target = "id", source = "id.value")
    AdoptanteResponse toResponse(Adoptante adoptante);

    @Mapping(target = "id", source = "id.value")
    AdoptanteEntity toEntity(Adoptante t);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapAdoptanteId")
    Adoptante toDomain(AdoptanteEntity e);

    List<Adoptante> toDomain(List<AdoptanteEntity> lista);

    @Named("mapAdoptanteId")
    default AdoptanteId mapAdoptanteId(Integer id) {
        return id != null ? new AdoptanteId(id) : null;
    }

    default EstadoValidacion mapEstadoValidacion(String estado) {
        if (estado == null) {
            return null;
        }
        return EstadoValidacion.valueOf(estado.toUpperCase());
    }
}