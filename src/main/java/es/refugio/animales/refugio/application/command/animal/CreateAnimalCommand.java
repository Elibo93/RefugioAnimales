package es.refugio.animales.refugio.application.command.animal;

public record CreateAnimalCommand(
        String nombre,
        String especie,
        String raza,
        String sexo,
        String chipId,
        String estado) {
}
