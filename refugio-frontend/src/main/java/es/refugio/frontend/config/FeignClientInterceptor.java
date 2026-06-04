package es.refugio.frontend.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FeignClientInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();

                // Reenviar el header Accept-Language para la i18n del backend
                Locale activeLocale = LocaleContextHolder.getLocale();
                if (activeLocale != null) {
                    template.header("Accept-Language", activeLocale.getLanguage());
                }

                // Buscar específicamente el JWT_TOKEN en los objetos Cookie
                boolean cookieFound = false;
                if (request.getCookies() != null) {
                    for (Cookie c : request.getCookies()) {
                        if ("JWT_TOKEN".equals(c.getName())) {
                            template.header("Authorization", "Bearer " + c.getValue());
                            logger.info("Retransmitiendo JWT como token Bearer a " + template.url());
                            cookieFound = true;
                            break;
                        }
                    }
                }

                if (!cookieFound) {
                    logger.warn("No se encontraron cookies JWT en la petición actual para retransmitir a " + template.url());
                }
            }
        } catch (IllegalStateException e) {
            // Sin contexto de request (ej. llamadas programadas)
            logger.debug("No hay contexto de petición para retransmitir cookie: " + e.getMessage());
        }
    }
}
