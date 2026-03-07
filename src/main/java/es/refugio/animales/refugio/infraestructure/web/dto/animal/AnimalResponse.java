package es.refugio.animales.refugio.infraestructure.web.dto.animal;

public record AnimalResponse(
        int id,
        String nombre,
        String especie,
        String raza,
        String sexo,
        String chipId,
        String estado,
        java.time.LocalDateTime createdAt) {

}
