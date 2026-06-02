package es.refugio.common.infraestructure.web.error;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataIntegrityViolationException;
import es.refugio.common.domain.error.EntityNotFoundException;

import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CustomResponse> nullPointerHandler(NullPointerException nfe) {
        String msg = messageSource.getMessage("common.error.null_pointer", new Object[]{nfe.getMessage()}, LocaleContextHolder.getLocale());
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

        String msg = messageSource.getMessage("common.error.validation", null, LocaleContextHolder.getLocale());
        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                msg,
                errors
        );
        return new ResponseEntity<>(cr, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> details = new HashMap<>();
        String message = ex.getMostSpecificCause().getMessage();
        String friendlyMessage = messageSource.getMessage("common.error.data_integrity", null, LocaleContextHolder.getLocale());

        if (message != null && message.contains("dni")) {
            friendlyMessage = messageSource.getMessage("common.error.dni_duplicate", null, LocaleContextHolder.getLocale());
            details.put("dni", messageSource.getMessage("common.error.duplicate", null, LocaleContextHolder.getLocale()));
        } else if (message != null && message.contains("usuario_id")) {
            friendlyMessage = messageSource.getMessage("common.error.usuario_duplicate", null, LocaleContextHolder.getLocale());
            details.put("usuarioId", messageSource.getMessage("common.error.duplicate", null, LocaleContextHolder.getLocale()));
        }

        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT,
                friendlyMessage,
                details
        );
        return new ResponseEntity<>(cr, HttpStatus.CONFLICT);
    }

    private String tryTranslate(String messageKey) {
        try {
            return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return messageKey;
        }
    }

    private String tryTranslate(String messageKey, Object[] args) {
        try {
            return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return messageKey;
        }
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomResponse> handleIllegalState(IllegalStateException ex) {
        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.PRECONDITION_FAILED,
                tryTranslate(ex.getMessage())
        );
        return new ResponseEntity<>(cr, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse> handleIllegalArgument(IllegalArgumentException ex) {
        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                tryTranslate(ex.getMessage())
        );
        return new ResponseEntity<>(cr, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomResponse> handleEntityNotFound(EntityNotFoundException ex) {
        String msg = tryTranslate("error.entity.not_found", new Object[]{ex.getEntityName()});
        if (ex.getEntityId() != null) {
            msg = tryTranslate("error.entity.not_found_id", new Object[]{ex.getEntityName(), ex.getEntityId()});
        }
        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                msg
        );
        return new ResponseEntity<>(cr, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> handleGenericException(Exception ex) {
        CustomResponse cr = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                tryTranslate(ex.getMessage())
        );
        return new ResponseEntity<>(cr, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
