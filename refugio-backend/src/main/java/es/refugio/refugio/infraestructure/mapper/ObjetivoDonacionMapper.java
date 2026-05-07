package es.refugio.refugio.infraestructure.mapper;

import org.springframework.stereotype.Component;

import es.refugio.refugio.domain.model.donacion.ObjetivoDonacion;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.infraestructure.db.jpa.entity.ObjetivoDonacionEntity;

@Component
public class ObjetivoDonacionMapper {

    public static ObjetivoDonacion toDomain(ObjetivoDonacionEntity entity) {
        if (entity == null) {
            return null;
        }
        return ObjetivoDonacion.builder()
                .id(new ObjetivoDonacionId(entity.getId()))
                .titulo(entity.getTitulo())
                .descripcion(entity.getDescripcion())
                .montoObjetivo(entity.getMontoObjetivo())
                .montoRecaudado(entity.getMontoRecaudado())
                .prioridad(entity.getPrioridad())
                .estado(entity.getEstado())
                .fechaInicio(entity.getFechaInicio())
                .fechaLimite(entity.getFechaLimite())
                .icono(entity.getIcono())
                .build();
    }

    public static ObjetivoDonacionEntity toEntity(ObjetivoDonacion domain) {
        if (domain == null) {
            return null;
        }
        return ObjetivoDonacionEntity.builder()
                .id(domain.getId() != null ? domain.getId().getValue() : null)
                .titulo(domain.getTitulo())
                .descripcion(domain.getDescripcion())
                .montoObjetivo(domain.getMontoObjetivo())
                .montoRecaudado(domain.getMontoRecaudado())
                .prioridad(domain.getPrioridad())
                .estado(domain.getEstado())
                .fechaInicio(domain.getFechaInicio())
                .fechaLimite(domain.getFechaLimite())
                .icono(domain.getIcono())
                .build();
    }
}
