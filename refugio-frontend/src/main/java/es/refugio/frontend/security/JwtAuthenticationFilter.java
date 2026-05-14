package es.refugio.frontend.security;

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

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                Claims claims = tokenProvider.getClaimsFromJWT(jwt);
                String username = claims.getSubject();
                String rolesStr = claims.get("roles", String.class);
                Integer usuarioId = null; Object o = claims.get("usuarioId"); if(o instanceof Number) usuarioId = ((Number)o).intValue();

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
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
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
