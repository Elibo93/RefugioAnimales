package es.refugio.refugio.infraestructure.web.dto.animal;

public record AnimalResponse(
        int id,
        String nombre,
        String especie,
        String raza,
        String sexo,
        String chipId,
        String estado,
        Integer edad,
        String tamano,
        String descripcion,
        String foto,
        java.time.LocalDateTime fechaIngreso) {

}
