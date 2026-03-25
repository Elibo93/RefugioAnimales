package es.refugio.refugio.application.command.animal;

import es.refugio.refugio.domain.model.animal.AnimalId;

public record EditAnimalCommand(
                AnimalId id,
                String nombre,
                String chipId,
                String estado,
                Integer edad,
                String tamano,
                String descripcion,
                String foto,
                Double peso,
                Integer nivelEnergia,
                Boolean urgencia) {
}