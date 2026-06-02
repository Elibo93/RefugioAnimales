package es.refugio.refugio.infraestructure.web.validation.adopcion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NombradoAdopcionValidator implements ConstraintValidator<NombradoAdopcion, String> {

    public final static String STR_BLANCO = " ";
    public final static String STR_SALTO = "\n";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        /*
         * Validación equivalente al ejemplo del Voluntario:
         * - No es nulo
         * - No está vacío
         * - No contiene espacios
         * - No contiene saltos de línea
         */

        if (value == null || value.length() == 0)
            return false;

        if (value.contains(STR_BLANCO))
            return false;

        if (value.contains(STR_SALTO))
            return false;

        return true;
    }
}
















