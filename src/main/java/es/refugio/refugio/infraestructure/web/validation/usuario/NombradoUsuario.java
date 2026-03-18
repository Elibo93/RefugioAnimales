package es.refugio.refugio.infraestructure.web.validation.usuario;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NombradoUsuarioValidator.class)
@Documented
public @interface NombradoUsuario {

    String message() default "{es.refugio.refugio.infraestructure.web.validation.NombradoUsuario}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}