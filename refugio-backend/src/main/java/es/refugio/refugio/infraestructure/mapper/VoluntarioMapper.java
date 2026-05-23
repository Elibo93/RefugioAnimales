package es.refugio.refugio.infraestructure.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.DisponibilidadVoluntario;
import es.refugio.refugio.domain.model.voluntario.DisponibilidadVoluntarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.DisponibilidadVoluntarioEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.web.dto.voluntario.DisponibilidadResponse;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioRequest;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioResponse;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioUpdateRequest;

@Mapper(componentModel = "spring")
public interface VoluntarioMapper {

    @Mapping(target = "usuarioId", source = "usuarioId", qualifiedByName = "mapUsuarioId")
    CreateVoluntarioCommand toCommand(VoluntarioRequest req);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapVoluntarioId")
    EditVoluntarioCommand toCommand(int id, VoluntarioRequest req);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapVoluntarioId")
    EditVoluntarioCommand toCommand(int id, VoluntarioUpdateRequest req);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "usuarioId", source = "usuarioId.value")
    VoluntarioResponse toResponse(Voluntario v);

    @Mapping(target = "id", source = "id.value")
    DisponibilidadResponse toResponse(DisponibilidadVoluntario d);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "usuarioId", source = "usuarioId.value")
    @Mapping(target = "status", source = "estado")
    @Mapping(target = "tareas", source = "tareas", qualifiedByName = "mapTareasToEntities")
    @Mapping(target = "disponibilidades", source = "disponibilidades", qualifiedByName = "mapDisponibilidadesToEntities")
    VoluntarioEntity toEntity(Voluntario v);

    @AfterMapping
    default void linkDisponibilidades(@MappingTarget VoluntarioEntity entity) {
        if (entity.getDisponibilidades() != null) {
            entity.getDisponibilidades().forEach(d -> d.setVoluntario(entity));
        }
    }

    @Mapping(target = "id", source = "id", qualifiedByName = "mapVoluntarioId")
    @Mapping(target = "usuarioId", source = "usuarioId", qualifiedByName = "mapUsuarioId")
    @Mapping(target = "estado", source = "status")
    @Mapping(target = "tareas", source = "tareas", qualifiedByName = "mapEntitiesToTareas")
    @Mapping(target = "disponibilidades", source = "disponibilidades", qualifiedByName = "mapEntitiesToDisponibilidades")
    Voluntario toDomain(VoluntarioEntity e);

    List<Voluntario> toDomain(List<VoluntarioEntity> entities);

    List<VoluntarioResponse> toResponse(List<Voluntario> voluntarios);

    @Named("mapVoluntarioId")
    default VoluntarioId mapVoluntarioId(Integer id) {
        return id != null ? new VoluntarioId(id) : null;
    }

    @Named("mapUsuarioId")
    default UsuarioId mapUsuarioId(Integer id) {
        return id != null ? new UsuarioId(id) : null;
    }

    @Named("mapTareasToEntities")
    default List<TareaEntity> mapTareasToEntities(List<TareaId> tareas) {
        if (tareas == null) {
            return null;
        }
        return tareas.stream().map(tid -> TareaEntity.builder().id(tid.getValue()).build()).toList();
    }

    @Named("mapEntitiesToTareas")
    default List<TareaId> mapEntitiesToTareas(List<TareaEntity> tareas) {
        if (tareas == null) {
            return new ArrayList<>();
        }
        return tareas.stream().map(te -> new TareaId(te.getId())).toList();
    }

    @Named("mapDisponibilidadesToEntities")
    default List<DisponibilidadVoluntarioEntity> mapDisponibilidadesToEntities(List<DisponibilidadVoluntario> disponibilidades) {
        if (disponibilidades == null) {
            return null;
        }
        return disponibilidades.stream().map(d -> DisponibilidadVoluntarioEntity.builder()
                .id(d.getId() != null ? d.getId().getValue() : null)
                .fecha(d.getFecha())
                .turno(d.getTurno())
                .estado(d.getEstado())
                .build()).toList();
    }

    @Named("mapEntitiesToDisponibilidades")
    default List<DisponibilidadVoluntario> mapEntitiesToDisponibilidades(List<DisponibilidadVoluntarioEntity> disponibilidades) {
        if (disponibilidades == null) {
            return new ArrayList<>();
        }
        return disponibilidades.stream().map(de -> DisponibilidadVoluntario.builder()
                .id(new DisponibilidadVoluntarioId(de.getId()))
                .voluntarioId(de.getVoluntario() != null ? new VoluntarioId(de.getVoluntario().getId()) : null)
                .fecha(de.getFecha())
                .turno(de.getTurno())
                .estado(de.getEstado())
                .build()).toList();
    }
}
