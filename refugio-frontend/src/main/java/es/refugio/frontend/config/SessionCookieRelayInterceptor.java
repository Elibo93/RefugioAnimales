package es.refugio.frontend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SessionCookieRelayInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest currentRequest = attrs.getRequest();

            // Reenviar todas las cookies del navegador al backend
            String cookie = currentRequest.getHeader("Cookie");
            if (cookie != null && !cookie.isBlank()) {
                logger.info("Relaying cookie to backend for " + request.getURI() + ": " + maskCookie(cookie));
                request.getHeaders().add("Cookie", cookie);
            } else {
                logger.warn("No cookie header found in current request attributes to relay to " + request.getURI());
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
