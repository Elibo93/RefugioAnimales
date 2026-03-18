package es.refugio.refugio.infraestructure.web.validation.adoptante;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NombradoAdoptanteValidator.class)
@Documented
public @interface NombradoAdoptante {

    // Mensaje por defecto (puedes personalizarlo en messages.properties)
    String message() default "El campo no debe contener espacios ni saltos de línea";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}