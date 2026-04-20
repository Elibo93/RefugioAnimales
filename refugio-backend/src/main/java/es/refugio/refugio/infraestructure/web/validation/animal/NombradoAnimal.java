package es.refugio.refugio.infraestructure.web.validation.animal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NombradoAnimalValidator.class)
@Documented
public @interface NombradoAnimal {

    String message() default "{es.refugio.refugio.infraestructure.web.validation.NombradoAnimal}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}