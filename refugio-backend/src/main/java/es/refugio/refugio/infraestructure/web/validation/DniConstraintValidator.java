package es.refugio.refugio.infraestructure.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DniConstraintValidator implements ConstraintValidator<ValidDni, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Validación desactivada temporalmente para pruebas según petición del usuario
        /* 
        if (value == null || value.isEmpty()) {
            return true; 
        }
        return DniValidator.isValid(value);
        */
        return true;
    }
}
