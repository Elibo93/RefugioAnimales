package es.refugio.frontend.web.dto;

public record AnimalRequest(
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
        Double peso,
        Integer nivelEnergia,
        Boolean urgencia,
        String fechaIngreso
) {
}
