package es.refugio.auth.infrastructure.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;

import java.util.List;

/**
 * Inicializador que garantiza que los usuarios del seed tienen contraseñas
 * codificadas con BCrypt correctamente.
 *
 * Ejecuta passwordEncoder.matches() para verificar que el hash almacenado
 * realmente corresponde a la contraseña por defecto. Si no coincide (hash
 * incorrecto o texto plano), la re-codifica. Es completamente idempotente.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class SeedPasswordInitializer implements CommandLineRunner {

    private static final String DEFAULT_SEED_PASSWORD = "password";

    private final UsuarioEntityJpaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();
        int actualizados = 0;

        for (UsuarioEntity usuario : usuarios) {
            // Los admins tienen contraseñas gestionadas por AdminInitializer, no los tocamos
            if (usuario.getRol() == es.refugio.auth.domain.Rol.ROLE_ADMIN) {
                continue;
            }

            String contrasena = usuario.getContrasena();
            // matches() verifica que el hash realmente corresponde a DEFAULT_SEED_PASSWORD
            boolean esValida = contrasena != null
                    && passwordEncoder.matches(DEFAULT_SEED_PASSWORD, contrasena);

            if (!esValida) {
                usuario.setContrasena(passwordEncoder.encode(DEFAULT_SEED_PASSWORD));
                usuarioRepository.save(usuario);
                actualizados++;
                log.info("[SeedPasswordInitializer] Contraseña recodificada para: {}", usuario.getEmail());
            }
        }

        if (actualizados > 0) {
            log.info("[SeedPasswordInitializer] {} contraseñas recodificadas correctamente.", actualizados);
        } else {
            log.info("[SeedPasswordInitializer] Todas las contraseñas de seed ya son correctas.");
        }
    }
}
