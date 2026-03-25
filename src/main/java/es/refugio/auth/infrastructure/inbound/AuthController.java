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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UsuarioEntityJpaRepository usuarioEntityJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("currentUri", "/login");
        model.addAttribute("showBack", true);
        model.addAttribute("content", "fragments/content/login");
        return "main-layout";
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        model.addAttribute("registroDto", new RegistroDto());
        model.addAttribute("currentUri", "/registro");
        model.addAttribute("showBack", true);
        model.addAttribute("content", "fragments/content/registro");
        return "main-layout";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("registroDto") RegistroDto registroDto,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("currentUri", "/registro");
            model.addAttribute("showBack", true);
            model.addAttribute("content", "fragments/content/registro");
            return "main-layout";
        }

        if (userRepository.findByEmail(registroDto.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "El email ya está registrado");
            model.addAttribute("currentUri", "/registro");
            model.addAttribute("showBack", true);
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

        // AUTO-LOGIN: Autenticar al usuario tras registro exitoso
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                registroDto.getEmail(), registroDto.getPassword());
        Authentication auth = authenticationManager.authenticate(token);
        
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        // Redirigir a la URL guardada por Spring Security si existe
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            return "redirect:" + savedRequest.getRedirectUrl();
        }

        return "redirect:/web/home?registroExitoso";
    }
}
