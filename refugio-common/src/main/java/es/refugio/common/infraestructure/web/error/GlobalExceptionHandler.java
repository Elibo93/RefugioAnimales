package es.refugio.common.infraestructure.web.error;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CustomResponse> nullPointerHandler(NullPointerException nfe) {
        String msg = messageSource.getMessage("common.error.null_pointer", new Object[]{nfe.getMessage()}, Locale.getDefault());
        CustomResponse cr = new CustomResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, msg);
        return new ResponseEntity<>(cr, cr.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Error de validación en los datos enviados",
                errors
        );
        return new ResponseEntity<>(cr, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponse> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        Map<String, Object> details = new HashMap<>();
        String message = ex.getMostSpecificCause().getMessage();
        String friendlyMessage = "Error de integridad de datos";

        if (message != null && message.contains("dni")) {
            friendlyMessage = "El DNI introducido ya existe en el sistema";
            details.put("dni", "Duplicado");
        } else if (message != null && message.contains("usuario_id")) {
            friendlyMessage = "Este usuario ya tiene un perfil legal asignado";
            details.put("usuarioId", "Duplicado");
        }

        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT,
                friendlyMessage,
                details
        );
        return new ResponseEntity<>(cr, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomResponse> handleIllegalState(IllegalStateException ex) {
        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.PRECONDITION_FAILED,
                ex.getMessage()
        );
        return new ResponseEntity<>(cr, HttpStatus.PRECONDITION_FAILED);
    }
}
