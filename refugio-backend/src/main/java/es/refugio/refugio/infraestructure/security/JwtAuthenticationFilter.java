package es.refugio.refugio.infraestructure.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            logger.info("Comprobando JWT desde la cookie en backend: " + (jwt != null ? "Encontrado (longitud " + jwt.length() + ")" : "No encontrado"));

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                Claims claims = tokenProvider.getClaimsFromJWT(jwt);
                String username = claims.getSubject();
                String rolesStr = claims.get("roles", String.class);
                
                logger.info("JWT validado para usuario: " + username + " con roles: " + rolesStr);
                
                Integer usuarioId = null; 
                Object o = claims.get("usuarioId"); 
                if(o instanceof Number) {
                    usuarioId = ((Number)o).intValue();
                } else if (o instanceof String) {
                    try {
                        usuarioId = Integer.parseInt((String)o);
                    } catch (NumberFormatException e) {
                        logger.error("Formato de usuarioId inválido en JWT: " + o);
                    }
                }
                
                logger.info("usuarioId resuelto desde token: " + usuarioId);

                if (rolesStr == null) {
                    rolesStr = "";
                }

                List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesStr.split(","))
                        .map(String::trim)
                        .filter(role -> !role.isEmpty())
                        .flatMap(role -> {
                            if ("ROLE_VOLUNTARIO_ADOPTANTE".equals(role)) {
                                return Stream.of(role, "ROLE_VOLUNTARIO", "ROLE_ADOPTANTE");
                            }
                            return Stream.of(role);
                        })
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                CustomUserDetails userDetails = new CustomUserDetails(username, usuarioId, authorities);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwt != null) {
                logger.warn("Se encontró JWT pero la validación falló en el backend.");
            }
        } catch (Exception ex) {
            logger.error("Error en JwtAuthenticationFilter: " + ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // 1. Intentar obtener de la cabecera Authorization: Bearer <token>
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. Intentar obtener de la cookie JWT_TOKEN
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "JWT_TOKEN".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
