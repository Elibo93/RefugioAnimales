package es.refugio.refugio.infraestructure.web.validation.animal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NombradoAnimalValidator implements ConstraintValidator<NombradoAnimal, String> {

    public final static String STR_BLANCO = " ";
    public final static String STR_SALTO = "\n";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty() || value.contains(STR_BLANCO) || value.contains(STR_SALTO)) {
            return false;
        }

        return true;
    }

}