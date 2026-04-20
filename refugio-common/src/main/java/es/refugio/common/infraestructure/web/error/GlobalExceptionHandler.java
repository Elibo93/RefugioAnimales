package es.refugio.common.infraestructure.web.error;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;

//Indicamos que es una gestora de errores
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    // Inyectamos el messageSource
    private final MessageSource messageSource;

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CustomResponse> nullPointerHandler(NullPointerException nfe) {

        // common.error.null_pointer = Se ha producido un error en el servidor: {0}
        Object[] datos = new Object[] {
                nfe // Parametro que se sustituirá a {0}
        };
        String msg = messageSource.getMessage(
                "common.error.null_pointer", // Mensaje que queremos
                datos, // Argumentos para los placeholders (si no hay null)
                Locale.getDefault()); // configuración regional, la que se esté usando.

        CustomResponse cr = new CustomResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, msg);
        return new ResponseEntity<>(cr, cr.getStatus());
    }
    
}
















