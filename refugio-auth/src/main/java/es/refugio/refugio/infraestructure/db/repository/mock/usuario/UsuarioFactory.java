package es.refugio.refugio.infraestructure.db.repository.mock.usuario;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import es.refugio.auth.domain.Rol;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

public class UsuarioFactory {

    public static final Map<UsuarioId, Usuario> getDemoData() {
        Map<UsuarioId, Usuario> datos = new LinkedHashMap<>();

        datos.put(new UsuarioId(1),
                Usuario.builder()
                        .id(new UsuarioId(1))
                        .email("diego@example.com")
                        .contrasena("pass123")
                        .rol(Rol.ROLE_ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build());

        datos.put(new UsuarioId(2),
                Usuario.builder()
                        .id(new UsuarioId(2))
                        .email("maria@example.com")
                        .contrasena("pass456")
                        .rol(Rol.ROLE_ADOPTANTE)
                        .createdAt(LocalDateTime.now())
                        .build());

        return datos;
    }

    public static Usuario create() {
        return Usuario.builder()
                .id(new UsuarioId(5))
                .email("prueba@example.com")
                .contrasena("prueba123")
                .rol(Rol.ROLE_ADOPTANTE)
                .createdAt(LocalDateTime.now())
                .build();
    }
}