package es.refugio.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.net.URI;

/**
 * Levanta un servidor HTTP secundario en el puerto 8080 (http.port) cuyo
 * único propósito es emitir un redirect 301 a HTTPS (server.port = 8443).
 *
 * Usa únicamente la API reactiva de Spring (HttpHandler), sin mezclar
 * la API raw de Reactor Netty.
 */
@Configuration
public class HttpToHttpsRedirectConfig {

    /** Puerto HTTP secundario (puerta de entrada sin cifrar). */
    @Value("${http.port:8080}")
    private int httpPort;

    /** Puerto HTTPS principal del Gateway. */
    @Value("${server.port:8443}")
    private int httpsPort;

    @Bean
    public WebServer httpRedirectServer() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory(httpPort);

        // HttpHandler lambda: (ServerHttpRequest, ServerHttpResponse) -> Mono<Void>
        return factory.getWebServer((request, response) -> {
            URI original = request.getURI();
            URI redirectUri;
            try {
                redirectUri = new URI(
                        "https", null,
                        original.getHost(), httpsPort,
                        original.getPath(),
                        original.getQuery(),
                        null
                );
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return response.setComplete();
            }
            response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            response.getHeaders().setLocation(redirectUri);
            return response.setComplete();
        });
    }
}
