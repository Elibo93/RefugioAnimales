package es.refugio.auth.infrastructure.inbound;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import es.refugio.auth.application.dto.RegistroDto;
import es.refugio.auth.infrastructure.repository.UserRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import es.refugio.auth.infrastructure.security.JwtTokenProvider;
import es.refugio.auth.domain.Rol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UsuarioEntityJpaRepository usuarioEntityJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    @PostMapping("/registro")
    public void procesarRegistro(
            @ModelAttribute RegistroDto registroDto,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // Validación manual ya que hemos retirado las aserciones de BindingResult
        // vinculadas a vistas HTML
        if (registroDto.getEmail() == null || registroDto.getEmail().isBlank() ||
                registroDto.getPassword() == null || registroDto.getPassword().isBlank()) {
            response.sendRedirect("/registro?error=Campos requeridos vacios");
            return;
        }

        if (userRepository.findByEmail(registroDto.getEmail()).isPresent()) {
            response.sendRedirect("/registro?error=El email ya esta registrado");
            return;
        }

        if (registroDto.getUsername() == null || registroDto.getUsername().isBlank()) {
            response.sendRedirect("/registro?error=El nombre de usuario es obligatorio");
            return;
        }

        if (userRepository.findByUsername(registroDto.getUsername()).isPresent()) {
            response.sendRedirect("/registro?error=El nombre de usuario ya esta registrado");
            return;
        }

        UsuarioEntity newUser = UsuarioEntity.builder()
                .email(registroDto.getEmail())
                .username(registroDto.getUsername())
                .contrasena(passwordEncoder.encode(registroDto.getPassword()))
                .rol(registroDto.getRol() == null ? Rol.ROLE_PUBLICO : registroDto.getRol())
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
        Cookie authCookie = new Cookie("JWT_TOKEN", jwtToken);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false); // establecer a true en producción
        authCookie.setPath("/");
        authCookie.setMaxAge(86400);
        response.addCookie(authCookie);

        // Generar e inyectar Cookie JWT para completar el auto-login en la arquitectura
        // Stateless
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
