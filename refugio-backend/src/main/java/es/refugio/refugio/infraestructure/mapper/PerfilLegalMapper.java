package es.refugio.refugio.infraestructure.mapper;

import es.refugio.refugio.domain.model.perfil_legal.PerfilLegal;
import es.refugio.refugio.domain.model.perfil_legal.PerfilLegalId;
import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import es.refugio.refugio.infraestructure.web.dto.perfil_legal.PerfilLegalRequest;
import es.refugio.refugio.infraestructure.web.dto.perfil_legal.PerfilLegalResponse;

public class PerfilLegalMapper {

    public static PerfilLegal toDomain(PerfilLegalRequest request) {
        if (request == null) return null;
        
        return PerfilLegal.builder()
                .usuarioId(request.usuarioId())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .dni(request.dni())
                .telefono(request.telefono())
                .direccion(request.direccion())
                .build();
    }

    public static PerfilLegal toDomain(PerfilLegalEntity entity) {
        if (entity == null) return null;

        return PerfilLegal.builder()
                .id(new PerfilLegalId(entity.getId()))
                .usuarioId(entity.getUsuarioId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .dni(entity.getDni())
                .telefono(entity.getTelefono())
                .direccion(entity.getDireccion())
                .build();
    }

    public static PerfilLegalEntity toEntity(PerfilLegal domain) {
        if (domain == null) return null;

        return PerfilLegalEntity.builder()
                .id(domain.getId() != null ? domain.getId().getValue() : null)
                .usuarioId(domain.getUsuarioId())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .dni(domain.getDni())
                .telefono(domain.getTelefono())
                .direccion(domain.getDireccion())
                .build();
    }

    public static PerfilLegalResponse toResponse(PerfilLegal perfil) {
        if (perfil == null) return null;
        
        return new PerfilLegalResponse(
                perfil.getUsuarioId(),
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getDni(),
                perfil.getTelefono(),
                perfil.getDireccion()
        );
    }
}
