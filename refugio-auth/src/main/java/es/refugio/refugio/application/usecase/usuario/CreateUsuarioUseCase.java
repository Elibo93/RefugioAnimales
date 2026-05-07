package es.refugio.refugio.application.usecase.usuario;

import java.time.LocalDateTime;
import es.refugio.refugio.application.command.usuario.CreateUsuarioCommand;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class CreateUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario create(CreateUsuarioCommand comando) {
        Usuario usuario = Usuario.builder()
                .email(comando.email())
                .username(comando.username())
                .contrasena(passwordEncoder.encode(comando.contrasena()))  // ← BCrypt siempre
                .rol(comando.rol())
                .createdAt(LocalDateTime.now())
                .build();

        return usuarioRepository.save(usuario);
    }
}