package es.refugio.refugio.infraestructure.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.application.command.tarea.EditTareaCommand;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaRequest;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaResponse;

@Mapper(componentModel = "spring")
public interface TareaMapper {

    CreateTareaCommand toCommand(TareaRequest req);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapTareaId")
    EditTareaCommand toCommand(int id, TareaRequest req);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "voluntarioIds", source = "voluntarios", qualifiedByName = "mapVoluntarioIdsToIntList")
    TareaResponse toResponse(Tarea t);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "voluntarios", source = "voluntarios", qualifiedByName = "mapVoluntarioIdsToEntities")
    TareaEntity toEntity(Tarea t);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapTareaId")
    @Mapping(target = "voluntarios", source = "voluntarios", qualifiedByName = "mapEntitiesToVoluntarioIds")
    Tarea toDomain(TareaEntity e);

    List<Tarea> toDomain(List<TareaEntity> entities);

    List<TareaResponse> toResponse(List<Tarea> tareas);

    @Named("mapTareaId")
    default TareaId mapTareaId(Integer id) {
        return id != null ? new TareaId(id) : null;
    }

    @Named("mapVoluntarioIdsToIntList")
    default List<Integer> mapVoluntarioIdsToIntList(List<VoluntarioId> voluntarios) {
        if (voluntarios == null) {
            return new ArrayList<>();
        }
        return voluntarios.stream().map(VoluntarioId::getValue).toList();
    }

    @Named("mapVoluntarioIdsToEntities")
    default List<VoluntarioEntity> mapVoluntarioIdsToEntities(List<VoluntarioId> voluntarios) {
        if (voluntarios == null) {
            return null;
        }
        return voluntarios.stream().map(vid -> VoluntarioEntity.builder().id(vid.getValue()).build()).toList();
    }

    @Named("mapEntitiesToVoluntarioIds")
    default List<VoluntarioId> mapEntitiesToVoluntarioIds(List<VoluntarioEntity> voluntarios) {
        if (voluntarios == null) {
            return new ArrayList<>();
        }
        return voluntarios.stream().map(ve -> new VoluntarioId(ve.getId())).toList();
    }
}
