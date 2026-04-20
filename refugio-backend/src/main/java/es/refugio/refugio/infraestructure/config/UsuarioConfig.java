package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.refugio.refugio.application.service.usuario.CreateUsuarioService;
import es.refugio.refugio.application.service.usuario.DeleteUsuarioService;
import es.refugio.refugio.application.service.usuario.EditUsuarioService;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.application.usecase.usuario.CreateUsuarioUseCase;
import es.refugio.refugio.application.usecase.usuario.DeleteUsuarioUseCase;
import es.refugio.refugio.application.usecase.usuario.EditUsuarioUseCase;
import es.refugio.refugio.application.usecase.usuario.FindUsuarioUseCase;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioJpaRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UsuarioConfig {

    private final UsuarioEntityJpaRepository usuarioEntityJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public UsuarioRepository usuarioRepository() {
        return new UsuarioJpaRepositoryImpl(usuarioEntityJpaRepository);
    }

    @Bean
    public CreateUsuarioUseCase createUsuarioUseCase() {
        return new CreateUsuarioUseCase(usuarioRepository(), passwordEncoder);
    }

    @Bean
    public CreateUsuarioService createUsuarioService() {
        return new CreateUsuarioService(createUsuarioUseCase());
    }

    @Bean
    public FindUsuarioUseCase findUsuarioUseCase() {
        return new FindUsuarioUseCase(usuarioRepository());
    }

    @Bean
    public FindUsuarioService findUsuarioService() {
        return new FindUsuarioService(findUsuarioUseCase());
    }

    @Bean
    public DeleteUsuarioUseCase deleteUsuarioUseCase() {
        return new DeleteUsuarioUseCase(usuarioRepository());
    }

    @Bean
    public DeleteUsuarioService deleteUsuarioService() {
        return new DeleteUsuarioService(deleteUsuarioUseCase());
    }

    @Bean
    public EditUsuarioUseCase editUsuarioUseCase() {
        return new EditUsuarioUseCase(usuarioRepository());
    }

    @Bean
    public EditUsuarioService editUsuarioService() {
        return new EditUsuarioService(editUsuarioUseCase());
    }
}