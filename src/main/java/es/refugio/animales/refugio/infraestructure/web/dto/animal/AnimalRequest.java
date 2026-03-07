package es.refugio.animales.refugio.infraestructure.web.dto.animal;

import es.refugio.animales.refugio.domain.model.animal.Animal;

public record AnimalRequest(
        String nombre,
        String especie,
        String raza,
        String sexo,
        String chipId,
        String estado) {

    public AnimalRequest(Animal t) {
        this(
                t.getNombre(),
                t.getEspecie(),
                t.getRaza(),
                t.getSexo(),
                t.getChipId(),
                t.getEstado());
    }
}
