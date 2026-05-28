package es.refugio.frontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio centralizado para resolver mensajes i18n.
 * Reemplaza la funcionalidad getMessage() de ViewControllerHelper.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getMessage(String code) {
        try {
            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return code;
        }
    }

    public String getMessage(String code, Object... args) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return code;
        }
    }
}
