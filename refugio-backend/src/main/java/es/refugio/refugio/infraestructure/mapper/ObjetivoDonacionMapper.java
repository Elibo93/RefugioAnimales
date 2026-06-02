package es.refugio.refugio.infraestructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.domain.model.donacion.ObjetivoDonacion;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.infraestructure.db.jpa.entity.ObjetivoDonacionEntity;

@Mapper(componentModel = "spring")
public interface ObjetivoDonacionMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "mapObjetivoDonacionId")
    ObjetivoDonacion toDomain(ObjetivoDonacionEntity entity);

    @Mapping(target = "id", source = "id.value")
    ObjetivoDonacionEntity toEntity(ObjetivoDonacion domain);

    List<ObjetivoDonacion> toDomain(List<ObjetivoDonacionEntity> entities);

    @Named("mapObjetivoDonacionId")
    default ObjetivoDonacionId mapObjetivoDonacionId(Integer id) {
        return id != null ? new ObjetivoDonacionId(id) : null;
    }
}
