package es.refugio.auth.infrastructure.inbound;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import es.refugio.auth.application.dto.RegistroDto;
import es.refugio.auth.infrastructure.repository.UserRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UsuarioEntityJpaRepository usuarioEntityJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("content", "fragments/content/login");
        return "main-layout";
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        model.addAttribute("registroDto", new RegistroDto());
        model.addAttribute("content", "fragments/content/registro");
        return "main-layout";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("registroDto") RegistroDto registroDto,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("content", "fragments/content/registro");
            return "main-layout";
        }

        if (userRepository.findByEmail(registroDto.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "El email ya está registrado");
            model.addAttribute("content", "fragments/content/registro");
            return "main-layout";
        }

        UsuarioEntity newUser = UsuarioEntity.builder()
                .nombre("Nuevo")
                .apellido("Usuario")
                .email(registroDto.getEmail())
                .contrasena(passwordEncoder.encode(registroDto.getPassword()))
                .rol(registroDto.getRol())
                .createdAt(LocalDateTime.now())
                .build();

        usuarioEntityJpaRepository.save(newUser);

        return "redirect:/login?registroExitoso";
    }
}
