package es.refugio.refugio.infraestructure.web.validation.adoptante;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NombradoAdoptanteValidator implements ConstraintValidator<NombradoAdoptante, String> {

    public final static String STR_BLANCO = " ";
    public final static String STR_SALTO = "\n";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        /*
         * Validación para campos críticos del Adoptante (como DNI):
         * - No es nulo ni vacío
         * - Sin espacios en blanco
         * - Sin saltos de línea
         */

        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        if (value.contains(STR_BLANCO)) {
            return false;
        }

        if (value.contains(STR_SALTO)) {
            return false;
        }

        return true;
    }
}