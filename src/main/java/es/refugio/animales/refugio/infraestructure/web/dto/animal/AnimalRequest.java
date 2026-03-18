package es.refugio.animales.refugio.infraestructure.web.dto.animal;

import es.refugio.animales.refugio.domain.model.animal.Animal;

public record AnimalRequest(
        String nombre,
        String especie,
        String raza,
        String sexo,
        String chipId,
        String estado,
        Integer edad,
        String tamano,
        String descripcion,
        String foto) {

    public AnimalRequest(Animal t) {
        this(
                t.getNombre(),
                t.getEspecie(),
                t.getRaza(),
                t.getSexo(),
                t.getChipId(),
                t.getEstado(),
                t.getEdad(),
                t.getTamano(),
                t.getDescripcion(),
                t.getFoto());
    }
}
