package es.refugio.refugio.infraestructure.web.validation.voluntario;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NombradoVoluntarioValidator.class)
@Documented
public @interface NombradoVoluntario {
     // Definimos el mensaje por defecto
    String message() default "{es.etg.daw.dawes.java.rest.restfull.voluntarios.infraestructure.web.validation.NombradoVoluntario}";

    // Permite agrupar validaciones. Es útil cuando quieres validar solo un
    // subconjunto de reglas
    // (por ejemplo, validar un Voluntario para "Creación" vs. para "Actualización").
    // El valor por defecto es para todos los casos.
    Class<?>[] groups() default {};

    // Una propiedad avanzada que permite definir metadatos o códigos de error
    // específicos al resultado de la validación.
    Class<? extends Payload>[] payload() default {};

}
















