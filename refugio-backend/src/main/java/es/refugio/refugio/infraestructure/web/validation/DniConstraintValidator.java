package es.refugio.refugio.infraestructure.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import es.refugio.refugio.domain.utils.DniValidator;

public class DniConstraintValidator implements ConstraintValidator<ValidDni, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; 
        }
        return DniValidator.isValid(value);
    }
}
