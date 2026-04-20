package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.usuario.CreateUsuarioCommand;
import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import es.refugio.refugio.infraestructure.web.dto.usuario.UsuarioRequest;
import es.refugio.refugio.infraestructure.web.dto.usuario.UsuarioResponse;

public class UsuarioMapper {

    public static CreateUsuarioCommand toCommand(UsuarioRequest req) {
        return new CreateUsuarioCommand(
                req.nombre(),
                req.apellido(),
                req.email(),
                req.contrasena(),
                req.telefono(),
                req.rol());
    }

    public static EditUsuarioCommand toCommand(int id, UsuarioRequest req) {
        return new EditUsuarioCommand(
                new UsuarioId(id),
                req.nombre(),
                req.apellido(),
                req.email(),
                req.telefono(),
                req.rol());
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId() != null ? usuario.getId().getValue() : 0,
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getCreatedAt());
    }

    public static UsuarioEntity toEntity(Usuario a) {
        return UsuarioEntity.builder()
                .id(a.getId() != null ? a.getId().getValue() : null)
                .nombre(a.getNombre())
                .apellido(a.getApellido())
                .email(a.getEmail())
                .contrasena(a.getContrasena())
                .telefono(a.getTelefono())
                .rol(a.getRol())
                .createdAt(a.getCreatedAt())
                .build();
    }

    public static Usuario toDomain(UsuarioEntity e) {
        return Usuario.builder()
                .id(e.getId() != null ? new UsuarioId(e.getId()) : null)
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .email(e.getEmail())
                .contrasena(e.getContrasena())
                .telefono(e.getTelefono())
                .rol(e.getRol())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public static List<Usuario> toDomain(List<UsuarioEntity> lista) {
        return lista.stream()
                .map(UsuarioMapper::toDomain)
                .collect(Collectors.toList());
    }
}