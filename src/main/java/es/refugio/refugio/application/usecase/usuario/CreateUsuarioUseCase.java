package es.refugio.refugio.application.usecase.usuario;

import java.time.LocalDateTime;
import es.refugio.auth.domain.Rol;
import es.refugio.refugio.application.command.usuario.CreateUsuarioCommand;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public Usuario create(CreateUsuarioCommand comando) {
        Usuario usuario = Usuario.builder()
                .nombre(comando.nombre())
                .apellido(comando.apellido())
                .email(comando.email())
                .contraseña(comando.contraseña())
                .telefono(comando.telefono())
                .rol(Rol.valueOf(comando.rol().toUpperCase()))
                .createdAt(LocalDateTime.now())
                .build();

        return usuarioRepository.save(usuario);
    }
}