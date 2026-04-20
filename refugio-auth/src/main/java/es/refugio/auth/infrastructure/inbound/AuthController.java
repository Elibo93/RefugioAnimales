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
    private final es.refugio.auth.infrastructure.security.JwtTokenProvider tokenProvider;

    private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    @PostMapping("/registro")
    public void procesarRegistro(
            @ModelAttribute RegistroDto registroDto,
            HttpServletRequest request,
            HttpServletResponse response) throws java.io.IOException {
        
        // Validación manual ya que hemos retirado las aserciones de BindingResult vinculadas a vistas HTML
        if (registroDto.getNombre() == null || registroDto.getNombre().isBlank() ||
            registroDto.getEmail() == null || registroDto.getEmail().isBlank() ||
            registroDto.getPassword() == null || registroDto.getPassword().isBlank()) {
            response.sendRedirect("/registro?error=Campos requeridos vacios");
            return;
        }

        if (userRepository.findByEmail(registroDto.getEmail()).isPresent()) {
            response.sendRedirect("/registro?error=El email ya esta registrado");
            return;
        }

        UsuarioEntity newUser = UsuarioEntity.builder()
                .nombre(registroDto.getNombre())
                .apellido(registroDto.getApellido())
                .email(registroDto.getEmail())
                .contrasena(passwordEncoder.encode(registroDto.getPassword()))
                .rol(registroDto.getRol() == null ? es.refugio.auth.domain.Rol.ROLE_VOLUNTARIO : registroDto.getRol())
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

        String jwtToken = tokenProvider.generateToken(auth);
        jakarta.servlet.http.Cookie authCookie = new jakarta.servlet.http.Cookie("JWT_TOKEN", jwtToken);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false); // set to true in prod
        authCookie.setPath("/");
        authCookie.setMaxAge(86400); 
        response.addCookie(authCookie);

        // Generar e inyectar Cookie JWT para completar el auto-login en la arquitectura Stateless
        // Usamos el tokenProvider que inyectamos en el constructor

        // Redirigir a la URL guardada por Spring Security si existe
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null && savedRequest.getRedirectUrl() != null) {
            response.sendRedirect(savedRequest.getRedirectUrl());
            return;
        }

        response.sendRedirect("/web/home?registroExitoso");
    }
}
