package es.refugio.refugio.infraestructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.domain.model.tarea.TareaHistorial;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaHistorialEntity;
import es.refugio.refugio.infraestructure.web.dto.tarea.TareaHistorialResponse;

@Mapper(componentModel = "spring")
public interface TareaHistorialMapper {

    @Mapping(target = "tareaId", source = "tareaId.value")
    TareaHistorialEntity toEntity(TareaHistorial h);

    @Mapping(target = "tareaId", source = "tareaId", qualifiedByName = "mapTareaId")
    TareaHistorial toDomain(TareaHistorialEntity e);

    List<TareaHistorial> toDomain(List<TareaHistorialEntity> entities);

    @Mapping(target = "tareaId", source = "h.tareaId.value")
    @Mapping(target = "usuarioNombre", source = "usuarioNombre")
    TareaHistorialResponse toResponse(TareaHistorial h, String usuarioNombre);

    @Named("mapTareaId")
    default TareaId mapTareaId(Integer id) {
        return id != null ? new TareaId(id) : null;
    }
}
