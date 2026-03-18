package es.refugio.refugio.application.command.animal;

import java.time.LocalDate;

public record CreateAnimalCommand(
        String nombre,
        String especie,
        String especiePersonalizada,
        String raza,
        String sexo,
        String chipId,
        String estado,
        Integer edad,
        String tamano,
        String descripcion,
        String foto,
        LocalDate fechaIngreso
) {
}