package es.refugio.refugio.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.domain.model.preferencia.PreferenciaAdopcion;
import es.refugio.refugio.domain.model.preferencia.PreferenciaAdopcionId;
import es.refugio.refugio.infraestructure.db.jpa.entity.PreferenciaAdopcionEntity;
import es.refugio.refugio.infraestructure.web.dto.preferencia.PreferenciaAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.preferencia.PreferenciaAdopcionResponse;

@Mapper(componentModel = "spring")
public interface PreferenciaAdopcionMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "integerToPreferenciaId")
    PreferenciaAdopcion toDomain(PreferenciaAdopcionEntity entity);

    @Mapping(target = "id", source = "id", qualifiedByName = "preferenciaIdToInteger")
    PreferenciaAdopcionEntity toEntity(PreferenciaAdopcion domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "notificacionesActivas", expression = "java(request.notificacionesActivas() != null ? request.notificacionesActivas() : true)")
    @Mapping(target = "encuestaOmitida", expression = "java(request.encuestaOmitida() != null ? request.encuestaOmitida() : false)")
    PreferenciaAdopcion toDomain(PreferenciaAdopcionRequest request);

    @Mapping(target = "id", source = "id", qualifiedByName = "preferenciaIdToInteger")
    PreferenciaAdopcionResponse toResponse(PreferenciaAdopcion domain);

    @Named("preferenciaIdToInteger")
    default Integer preferenciaIdToInteger(PreferenciaAdopcionId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("integerToPreferenciaId")
    default PreferenciaAdopcionId integerToPreferenciaId(Integer id) {
        return id != null ? new PreferenciaAdopcionId(id) : null;
    }

    default es.refugio.refugio.domain.model.animal.enums.Especie mapEspecie(String s) {
        if (s == null) return null;
        return es.refugio.refugio.domain.model.animal.enums.Especie.valueOf(s.toUpperCase());
    }

    default es.refugio.refugio.domain.model.animal.enums.Tamano mapTamano(String s) {
        if (s == null) return null;
        return es.refugio.refugio.domain.model.animal.enums.Tamano.valueOf(s.toUpperCase());
    }

    default es.refugio.refugio.domain.model.animal.enums.Sexo mapSexo(String s) {
        if (s == null) return null;
        return es.refugio.refugio.domain.model.animal.enums.Sexo.valueOf(s.toUpperCase());
    }
}
