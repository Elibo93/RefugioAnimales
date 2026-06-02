package es.refugio.refugio.infraestructure.web.validation.voluntario;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NombradoVoluntarioValidator implements ConstraintValidator<NombradoVoluntario, String> {
     public final static String STR_BLANCO = " ";
    public final static String STR_SALTO = "\n";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        /*
         * Vamos a validar el campo Voluntario aplicando las siguientes normas:
         * No es nulo ni vacío
         */
        if (value == null || value.length() == 0 || value.contains(STR_BLANCO))
            return false;

        return true;
    }

}
















