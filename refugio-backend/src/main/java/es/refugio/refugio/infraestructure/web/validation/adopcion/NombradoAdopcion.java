package es.refugio.refugio.infraestructure.web.validation.adopcion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NombradoAdopcionValidator.class)
@Documented
public @interface NombradoAdopcion {

    // Mensaje por defecto
    String message() default "{es.etg.daw.dawes.java.refugio.infraestructure.web.validation.NombradoAdopcion}";

    // Permite agrupar validaciones (igual que en Voluntario)
    Class<?>[] groups() default {};

    // Permite definir metadatos adicionales en la validación
    Class<? extends Payload>[] payload() default {};
}
















