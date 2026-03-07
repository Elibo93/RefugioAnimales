package es.refugio.animales.refugio.application.command.voluntario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class CreateVoluntarioCommand {

    // Atributos
    private String nombre;
    private String apellido;
    private String especialidad;
    private String email;
    private String telefono;

}

















