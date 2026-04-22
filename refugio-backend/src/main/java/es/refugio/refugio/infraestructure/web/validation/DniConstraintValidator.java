package es.refugio.refugio.infraestructure.web.validation;

import es.refugio.refugio.domain.utils.DniValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DniConstraintValidator implements ConstraintValidator<ValidDni, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Si es nulo, dejamos que @NotNull lo maneje si fuera necesario.
        // Aquí solo validamos el formato si hay un valor.
        if (value == null || value.isEmpty()) {
            return true; 
        }
        
        return DniValidator.isValid(value);
    }
}
