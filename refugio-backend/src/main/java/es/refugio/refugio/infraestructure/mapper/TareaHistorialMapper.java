package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;
import es.refugio.refugio.domain.model.tarea.TareaHistorial;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaHistorialEntity;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaHistorialResponse;

public class TareaHistorialMapper {

    public static TareaHistorialEntity toEntity(TareaHistorial h) {
        return TareaHistorialEntity.builder()
                .id(h.getId())
                .tareaId(h.getTareaId().getValue())
                .estadoAnterior(h.getEstadoAnterior())
                .estadoNuevo(h.getEstadoNuevo())
                .usuarioId(h.getUsuarioId())
                .fechaCambio(h.getFechaCambio())
                .observaciones(h.getObservaciones())
                .build();
    }

    public static TareaHistorial toDomain(TareaHistorialEntity e) {
        return TareaHistorial.builder()
                .id(e.getId())
                .tareaId(new TareaId(e.getTareaId()))
                .estadoAnterior(e.getEstadoAnterior())
                .estadoNuevo(e.getEstadoNuevo())
                .usuarioId(e.getUsuarioId())
                .fechaCambio(e.getFechaCambio())
                .observaciones(e.getObservaciones())
                .build();
    }

    public static List<TareaHistorial> toDomain(List<TareaHistorialEntity> entities) {
        return entities.stream().map(TareaHistorialMapper::toDomain).collect(Collectors.toList());
    }

    public static TareaHistorialResponse toResponse(TareaHistorial h, String usuarioNombre) {
        return new TareaHistorialResponse(
                h.getId(),
                h.getTareaId().getValue(),
                h.getEstadoAnterior() != null ? h.getEstadoAnterior().name() : null,
                h.getEstadoNuevo().name(),
                h.getUsuarioId(),
                usuarioNombre,
                h.getFechaCambio(),
                h.getObservaciones()
        );
    }
}
