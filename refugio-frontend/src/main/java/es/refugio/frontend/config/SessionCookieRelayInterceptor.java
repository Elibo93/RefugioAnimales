package es.refugio.frontend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Interceptor que captura la cookie de sesión (JSESSIONID) del navegador
 * y la reenvía en cada llamada RestTemplate al backend.
 *
 * Así el backend puede identificar al usuario autenticado aunque sea
 * el frontend quien haga la llamada HTTP.
 */
@Component
public class SessionCookieRelayInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SessionCookieRelayInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest currentRequest = attrs.getRequest();

            // 1. Reenviar el header Cookie completo si existe
            String cookieHeader = currentRequest.getHeader("Cookie");
            if (cookieHeader != null && !cookieHeader.isBlank()) {
                request.getHeaders().add("Cookie", cookieHeader);
            }

            // 2. Buscar específicamente el JWT_TOKEN en los objetos Cookie
            // Esto es más robusto que parsear el header manual o depender de que el header no sea null
            if (currentRequest.getCookies() != null) {
                for (Cookie c : currentRequest.getCookies()) {
                    if ("JWT_TOKEN".equals(c.getName())) {
                        request.getHeaders().setBearerAuth(c.getValue());
                        logger.info("Relaying JWT as Bearer token to " + request.getURI());
                    }
                }
            }

            if (cookieHeader == null && currentRequest.getCookies() == null) {
                logger.warn("No cookies found in current request to relay to " + request.getURI());
            }
        } catch (IllegalStateException e) {
            // Sin contexto de request (tests, llamadas fuera de un hilo HTTP) — ignorar
            logger.debug("No request context for cookie relay: " + e.getMessage());
        }
        return execution.execute(request, body);
    }

    private String maskCookie(String cookie) {
        if (cookie == null) return null;
        if (cookie.length() < 10) return "****";
        return cookie.substring(0, 5) + "..." + cookie.substring(cookie.length() - 5);
    }
}
