package es.refugio.refugio.infraestructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.application.command.donacion.EditDonacionCommand;
import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.DonacionEntity;
import es.refugio.refugio.infraestructure.web.dto.donacion.DonacionRequest;
import es.refugio.refugio.infraestructure.web.dto.donacion.DonacionResponse;

@Mapper(componentModel = "spring")
public interface DonacionMapper {

    CreateDonacionCommand toCommand(DonacionRequest req);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapDonacionId")
    EditDonacionCommand toCommand(int id, DonacionRequest req);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "usuarioId", source = "usuarioId.value")
    @Mapping(target = "objetivoId", source = "objetivoId.value")
    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "frecuencia", source = "frecuencia")
    DonacionResponse toResponse(Donacion d);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "usuarioId", source = "usuarioId.value")
    @Mapping(target = "objetivoId", source = "objetivoId.value")
    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "frecuencia", source = "frecuencia")
    DonacionEntity toEntity(Donacion d);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapDonacionId")
    @Mapping(target = "usuarioId", source = "usuarioId", qualifiedByName = "mapUsuarioId")
    @Mapping(target = "objetivoId", source = "objetivoId", qualifiedByName = "mapObjetivoDonacionId")
    @Mapping(target = "tipo", source = "tipo", qualifiedByName = "mapTipo")
    @Mapping(target = "frecuencia", source = "frecuencia", qualifiedByName = "mapFrecuencia")
    Donacion toDomain(DonacionEntity e);

    List<Donacion> toDomain(List<DonacionEntity> entities);

    List<DonacionResponse> toResponse(List<Donacion> donaciones);

    @Named("mapDonacionId")
    default DonacionId mapDonacionId(Integer id) {
        return id != null ? new DonacionId(id) : null;
    }

    @Named("mapUsuarioId")
    default UsuarioId mapUsuarioId(Integer id) {
        return id != null ? new UsuarioId(id) : null;
    }

    @Named("mapObjetivoDonacionId")
    default ObjetivoDonacionId mapObjetivoDonacionId(Integer id) {
        return id != null ? new ObjetivoDonacionId(id) : null;
    }

    @Named("mapTipo")
    default TipoDonacion mapTipo(String tipo) {
        if (tipo == null) return null;
        try {
            return TipoDonacion.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            return TipoDonacion.OTRO;
        }
    }

    @Named("mapFrecuencia")
    default FrecuenciaDonacion mapFrecuencia(String frecuencia) {
        if (frecuencia == null) return null;
        try {
            return FrecuenciaDonacion.valueOf(frecuencia);
        } catch (IllegalArgumentException e) {
            return FrecuenciaDonacion.UNICA;
        }
    }
}
