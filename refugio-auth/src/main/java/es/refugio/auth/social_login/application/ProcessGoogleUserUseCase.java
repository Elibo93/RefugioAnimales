package es.refugio.auth.social_login.application;

import es.refugio.auth.domain.AuthCredentialEntity;
import es.refugio.auth.domain.Rol;
import es.refugio.auth.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessGoogleUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthCredentialEntity execute(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Usuario con email {} no existe. Registrando automáticamente vía Google OAuth2.", email);
                    
                    // Generar un username único a partir del email o nombre
                    String cleanName = name != null ? name.replaceAll("\\s+", "_").toLowerCase() : "user";
                    String uniqueUsername = cleanName + "_" + UUID.randomUUID().toString().substring(0, 6);

                    // Generar una contraseña temporal aleatoria encriptada ya que 'contrasena' no acepta nulos en base de datos
                    String tempPassword = passwordEncoder.encode(UUID.randomUUID().toString());

                    AuthCredentialEntity newUser = AuthCredentialEntity.builder()
                            .email(email)
                            .username(uniqueUsername)
                            .password(tempPassword)
                            .rol(Rol.ROLE_PUBLICO) // Rol por defecto inicial del sistema
                            .build();

                    return userRepository.save(newUser);
                });
    }
}
