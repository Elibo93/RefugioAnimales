package es.refugio.refugio.infraestructure.web.dto.animal;

import java.time.LocalDateTime;

public record AnimalResponse(
        int id,
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
        LocalDateTime fechaIngreso,
        Double peso,
        Integer nivelEnergia,
        Boolean urgencia
) {
}