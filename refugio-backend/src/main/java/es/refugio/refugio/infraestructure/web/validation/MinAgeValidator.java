package es.refugio.refugio.infraestructure.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

public class MinAgeValidator implements ConstraintValidator<MinAge, String> {

    private int minAge;

    @Override
    public void initialize(MinAge constraintAnnotation) {
        this.minAge = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Dejamos que @NotBlank lo maneje si es necesario
        }

        try {
            LocalDate birthDate = LocalDate.parse(value);
            LocalDate now = LocalDate.now();
            return Period.between(birthDate, now).getYears() >= minAge;
        } catch (DateTimeParseException e) {
            // Formato de fecha no válido
            return false;
        }
    }
}
