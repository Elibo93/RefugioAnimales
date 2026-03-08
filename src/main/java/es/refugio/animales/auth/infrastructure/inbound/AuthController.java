package es.refugio.animales.auth.infrastructure.inbound;

import es.refugio.animales.auth.application.dto.RegistroDto;
import es.refugio.animales.auth.domain.UserEntity;
import es.refugio.animales.auth.infrastructure.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
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

        UserEntity newUser = UserEntity.builder()
                .email(registroDto.getEmail())
                .password(passwordEncoder.encode(registroDto.getPassword()))
                .rol(registroDto.getRol())
                .build();

        userRepository.save(newUser);

        return "redirect:/login?registroExitoso";
    }
}
