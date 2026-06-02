package es.refugio.frontend.web.dto;

import java.time.LocalDateTime;

public record AnimalRecord(
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
        Boolean urgencia,
        Integer visitas,
        Integer conteoSolicitudes
) {}
