package es.refugio.refugio.application.command.animal;

public record CreateAnimalCommand(
        String nombre,
        String especie,
        String raza,
        String sexo,
        String chipId,
        String estado,
        Integer edad,
        String tamano,
        String descripcion,
        String foto
        ) {
}
