package es.refugio.refugio.infraestructure.mapper;

import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.Sexo;
import es.refugio.refugio.domain.model.animal.enums.Tamano;
import es.refugio.refugio.domain.model.preferencia.PreferenciaAdopcion;
import es.refugio.refugio.domain.model.preferencia.PreferenciaAdopcionId;
import es.refugio.refugio.infraestructure.db.jpa.entity.PreferenciaAdopcionEntity;
import es.refugio.refugio.infraestructure.web.dto.preferencia.PreferenciaAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.preferencia.PreferenciaAdopcionResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class PreferenciaAdopcionMapper {

    public PreferenciaAdopcion toDomain(PreferenciaAdopcionEntity entity) {
        if (entity == null) {
            return null;
        }

        return PreferenciaAdopcion.builder()
                .id(new PreferenciaAdopcionId(entity.getId()))
                .usuarioId(entity.getUsuarioId())
                .especies(new ArrayList<>(entity.getEspecies()))
                .tamanos(new ArrayList<>(entity.getTamanos()))
                .sexos(new ArrayList<>(entity.getSexos()))
                .edadMax(entity.getEdadMax())
                .nivelEnergiaMax(entity.getNivelEnergiaMax())
                .notificacionesActivas(entity.getNotificacionesActivas())
                .encuestaOmitida(entity.getEncuestaOmitida())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PreferenciaAdopcionEntity toEntity(PreferenciaAdopcion domain) {
        if (domain == null) {
            return null;
        }

        return PreferenciaAdopcionEntity.builder()
                .id(domain.getId() != null ? domain.getId().getValue() : null)
                .usuarioId(domain.getUsuarioId())
                .especies(domain.getEspecies() != null ? new ArrayList<>(domain.getEspecies()) : new ArrayList<>())
                .tamanos(domain.getTamanos() != null ? new ArrayList<>(domain.getTamanos()) : new ArrayList<>())
                .sexos(domain.getSexos() != null ? new ArrayList<>(domain.getSexos()) : new ArrayList<>())
                .edadMax(domain.getEdadMax())
                .nivelEnergiaMax(domain.getNivelEnergiaMax())
                .notificacionesActivas(domain.isNotificacionesActivas())
                .encuestaOmitida(domain.isEncuestaOmitida())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public PreferenciaAdopcion toDomain(PreferenciaAdopcionRequest request) {
        if (request == null) {
            return null;
        }

        return PreferenciaAdopcion.builder()
                .usuarioId(request.usuarioId())
                .especies(request.especies() != null ? request.especies().stream().map(Especie::valueOf).collect(Collectors.toList()) : new ArrayList<>())
                .tamanos(request.tamanos() != null ? request.tamanos().stream().map(Tamano::valueOf).collect(Collectors.toList()) : new ArrayList<>())
                .sexos(request.sexos() != null ? request.sexos().stream().map(Sexo::valueOf).collect(Collectors.toList()) : new ArrayList<>())
                .edadMax(request.edadMax())
                .nivelEnergiaMax(request.nivelEnergiaMax())
                .notificacionesActivas(request.notificacionesActivas() != null ? request.notificacionesActivas() : true)
                .encuestaOmitida(request.encuestaOmitida() != null ? request.encuestaOmitida() : false)
                .build();
    }

    public PreferenciaAdopcionResponse toResponse(PreferenciaAdopcion domain) {
        if (domain == null) {
            return null;
        }

        return new PreferenciaAdopcionResponse(
                domain.getId() != null ? domain.getId().getValue() : null,
                domain.getUsuarioId(),
                domain.getEspecies() != null ? domain.getEspecies().stream().map(Enum::name).collect(Collectors.toList()) : new ArrayList<>(),
                domain.getTamanos() != null ? domain.getTamanos().stream().map(Enum::name).collect(Collectors.toList()) : new ArrayList<>(),
                domain.getSexos() != null ? domain.getSexos().stream().map(Enum::name).collect(Collectors.toList()) : new ArrayList<>(),
                domain.getEdadMax(),
                domain.getNivelEnergiaMax(),
                domain.isNotificacionesActivas(),
                domain.isEncuestaOmitida(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
