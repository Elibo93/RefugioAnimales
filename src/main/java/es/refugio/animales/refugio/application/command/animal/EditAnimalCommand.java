package es.refugio.animales.refugio.application.command.animal;

import es.refugio.animales.refugio.domain.model.animal.AnimalId;

public record EditAnimalCommand(
        AnimalId id,
        String nombre,
        String especie,
        String raza,
        String sexo,
        String chipId,
        String estado,
        String notas) {
}
