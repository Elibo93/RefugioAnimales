package es.refugio.refugio.infraestructure.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.application.command.tarea.EditTareaCommand;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaRequest;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaResponse;

public class TareaMapper {

    public static CreateTareaCommand toCommand(TareaRequest req) {
        return new CreateTareaCommand(
                req.descripcion(),
                req.fecha(),
                req.estado(),
                req.fechaLimite(),
                req.instrucciones(),
                req.voluntarioIds()
        );
    }

    public static EditTareaCommand toCommand(int id, TareaRequest req) {
        return new EditTareaCommand(
                new TareaId(id),
                req.descripcion(),
                req.fecha(),
                req.estado(),
                req.fechaLimite(),
                req.instrucciones(),
                req.voluntarioIds(),
                req.voluntarioActorId()
        );
    }

    public static TareaResponse toResponse(Tarea t) {
        return new TareaResponse(
                t.getId() != null ? t.getId().getValue() : null,
                t.getDescripcion(),
                t.getFecha(),
                t.getEstado() != null ? t.getEstado().name() : null,
                t.getFechaLimite(),
                t.getInstrucciones(),
                t.getPrioridad(),
                t.getVoluntarios() != null ? 
                    t.getVoluntarios().stream().map(VoluntarioId::getValue).collect(Collectors.toList()) : 
                    new ArrayList<>()
        );
    }

    public static TareaEntity toEntity(Tarea t) {
        List<VoluntarioEntity> voluntarios = null;
        if (t.getVoluntarios() != null) {
            voluntarios = t.getVoluntarios().stream()
                    .map(vid -> VoluntarioEntity.builder().id(vid.getValue()).build())
                    .collect(Collectors.toList());
        }

        return TareaEntity.builder()
                .id(t.getId() != null ? t.getId().getValue() : null)
                .descripcion(t.getDescripcion())
                .fecha(t.getFecha())
                .estado(t.getEstado())
                .fechaLimite(t.getFechaLimite())
                .instrucciones(t.getInstrucciones())
                .voluntarios(voluntarios)
                .build();
    }

    public static Tarea toDomain(TareaEntity e) {
        return Tarea.builder()
                .id(e.getId() != null ? new TareaId(e.getId()) : null)
                .descripcion(e.getDescripcion())
                .fecha(e.getFecha())
                .estado(e.getEstado())
                .fechaLimite(e.getFechaLimite())
                .instrucciones(e.getInstrucciones())
                .voluntarios(e.getVoluntarios() != null ? 
                    e.getVoluntarios().stream().map(ve -> new VoluntarioId(ve.getId())).collect(Collectors.toList()) : 
                    new ArrayList<>())
                .build();
    }

    public static List<Tarea> toDomain(List<TareaEntity> entities) {
        return entities.stream().map(TareaMapper::toDomain).collect(Collectors.toList());
    }

    public static List<TareaResponse> toResponse(List<Tarea> tareas) {
        return tareas.stream().map(TareaMapper::toResponse).collect(Collectors.toList());
    }
}
