package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioRequest;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioResponse;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioUpdateRequest;
import es.refugio.refugio.infraestructure.web.dto.voluntario.DisponibilidadResponse;
import es.refugio.refugio.domain.model.voluntario.DisponibilidadVoluntario;
import es.refugio.refugio.domain.model.voluntario.DisponibilidadVoluntarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.DisponibilidadVoluntarioEntity;

public class VoluntarioMapper {

    public static CreateVoluntarioCommand toCommand(VoluntarioRequest req) {
        return new CreateVoluntarioCommand(
                new UsuarioId(req.usuarioId()),
                req.disponibilidad(),
                req.especialidad());
    }

    public static EditVoluntarioCommand toCommand(int id, VoluntarioRequest req) {
        return new EditVoluntarioCommand(
                new VoluntarioId(id),
                req.disponibilidad(),
                req.especialidad());
    }

    public static EditVoluntarioCommand toCommand(int id, VoluntarioUpdateRequest req) {
        return new EditVoluntarioCommand(
                new VoluntarioId(id),
                req.disponibilidad(),
                req.especialidad());
    }

    public static VoluntarioResponse toResponse(Voluntario v) {
        return new VoluntarioResponse(
                v.getId() != null ? v.getId().getValue() : null,
                v.getUsuarioId() != null ? v.getUsuarioId().getValue() : null,
                v.getDisponibilidad(),
                v.getEspecialidad(),
                v.getEstado() != null ? v.getEstado().name() : null);
    }

    public static DisponibilidadResponse toResponse(DisponibilidadVoluntario d) {
        return new DisponibilidadResponse(
                d.getId() != null ? d.getId().getValue() : null,
                d.getFecha(),
                d.getTurno(),
                d.getEstado());
    }

    public static VoluntarioEntity toEntity(Voluntario v) {
        Integer usuarioId = null;
        if (v.getUsuarioId() != null) {
            usuarioId = v.getUsuarioId().getValue();
        }

        List<TareaEntity> tareas = null;
        if (v.getTareas() != null) {
            tareas = v.getTareas().stream()
                    .map(tid -> TareaEntity.builder().id(tid.getValue()).build())
                    .collect(Collectors.toList());
        }

        VoluntarioEntity entity = VoluntarioEntity.builder()
                .id(v.getId() != null ? v.getId().getValue() : null)
                .usuarioId(usuarioId)
                .disponibilidad(v.getDisponibilidad())
                .especialidad(v.getEspecialidad())
                .status(v.getEstado())
                .tareas(tareas)
                .build();

        if (v.getDisponibilidades() != null) {
            List<DisponibilidadVoluntarioEntity> disponibilidades = v.getDisponibilidades().stream()
                    .map(d -> DisponibilidadVoluntarioEntity.builder()
                            .id(d.getId() != null ? d.getId().getValue() : null)
                            .fecha(d.getFecha())
                            .turno(d.getTurno())
                            .estado(d.getEstado())
                            .voluntario(entity)
                            .build())
                    .collect(Collectors.toList());
            entity.setDisponibilidades(disponibilidades);
        }

        return entity;
    }

    public static Voluntario toDomain(VoluntarioEntity e) {
        return Voluntario.builder()
                .id(e.getId() != null ? new VoluntarioId(e.getId()) : null)
                .usuarioId(e.getUsuarioId() != null ? new UsuarioId(e.getUsuarioId()) : null)
                .disponibilidad(e.getDisponibilidad())
                .especialidad(e.getEspecialidad())
                .estado(e.getStatus())
                .tareas(e.getTareas() != null
                        ? e.getTareas().stream().map(te -> new TareaId(te.getId())).collect(Collectors.toList())
                        : new java.util.ArrayList<>())
                .disponibilidades(e.getDisponibilidades() != null
                        ? e.getDisponibilidades().stream().map(de -> DisponibilidadVoluntario.builder()
                                .id(new DisponibilidadVoluntarioId(de.getId()))
                                .voluntarioId(new VoluntarioId(e.getId()))
                                .fecha(de.getFecha())
                                .turno(de.getTurno())
                                .estado(de.getEstado())
                                .build()).collect(Collectors.toList())
                        : new java.util.ArrayList<>())
                .build();
    }

    public static List<Voluntario> toDomain(List<VoluntarioEntity> entities) {
        return entities.stream().map(VoluntarioMapper::toDomain).collect(Collectors.toList());
    }

    public static List<VoluntarioResponse> toResponse(List<Voluntario> voluntarios) {
        return voluntarios.stream().map(VoluntarioMapper::toResponse).collect(Collectors.toList());
    }
}
