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
            logger.info("Checking JWT from cookie in backend: " + (jwt != null ? "Found (length " + jwt.length() + ")" : "Not Found"));

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                Claims claims = tokenProvider.getClaimsFromJWT(jwt);
                String username = claims.getSubject();
                String rolesStr = claims.get("roles", String.class);
                
                logger.info("JWT Validated for user: " + username + " with roles: " + rolesStr);
                
                Integer usuarioId = null; 
                Object o = claims.get("usuarioId"); 
                if(o instanceof Number) {
                    usuarioId = ((Number)o).intValue();
                } else if (o instanceof String) {
                    try {
                        usuarioId = Integer.parseInt((String)o);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid usuarioId format in JWT: " + o);
                    }
                }
                
                logger.info("Resolved usuarioId from token: " + usuarioId);

                if (rolesStr == null) {
                    rolesStr = "";
                }

                List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesStr.split(","))
                        .map(String::trim)
                        .filter(role -> !role.isEmpty())
                        .flatMap(role -> {
                            if ("ROLE_VOLUNTARIO_ADOPTANTE".equals(role)) {
                                return java.util.stream.Stream.of(role, "ROLE_VOLUNTARIO", "ROLE_ADOPTANTE");
                            }
                            return java.util.stream.Stream.of(role);
                        })
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                CustomUserDetails userDetails = new CustomUserDetails(username, usuarioId, authorities);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwt != null) {
                logger.warn("JWT was found but validation failed in backend.");
            }
        } catch (Exception ex) {
            logger.error("Error in JwtAuthenticationFilter: " + ex.getMessage(), ex);
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
