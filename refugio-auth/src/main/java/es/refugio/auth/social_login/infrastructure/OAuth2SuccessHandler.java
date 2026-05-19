package es.refugio.auth.social_login.infrastructure;

import es.refugio.auth.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 1. Generar token JWT interno
        String token = tokenProvider.generateToken(authentication);

        // 2. Adjuntar el JWT en la cookie de seguridad HttpOnly
        Cookie authCookie = new Cookie("JWT_TOKEN", token);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false); // Cambiar a true en producción (con HTTPS)
        authCookie.setPath("/");
        authCookie.setMaxAge(86400); // 1 día
        response.addCookie(authCookie);

        // 3. Determinar URL destino respetando el host de reenvío del Gateway
        String targetUrl = determineTargetUrl(request);

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request) {
        String proto = request.getHeader("X-Forwarded-Proto");
        String host = request.getHeader("X-Forwarded-Host");

        if (proto != null && !proto.isBlank() && host != null && !host.isBlank()) {
            return proto + "://" + host + "/web/home?socialLoginSuccess";
        }
        return "/web/home?socialLoginSuccess";
    }
}
