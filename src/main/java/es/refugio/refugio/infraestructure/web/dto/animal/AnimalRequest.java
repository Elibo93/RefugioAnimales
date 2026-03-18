package es.refugio.refugio.infraestructure.web.dto.animal;

import java.time.LocalDate;
import es.refugio.refugio.domain.model.animal.Animal;

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
        LocalDate fechaIngreso
) {

    public AnimalRequest(Animal t) {
        this(
                t.getNombre(),
                t.getEspecie() != null ? t.getEspecie().name() : null,
                t.getEspeciePersonalizada(),
                t.getRaza(),
                t.getSexo() != null ? t.getSexo().name() : null,
                t.getChipId(),
                t.getEstado() != null ? t.getEstado().name() : null,
                t.getEdad(),
                t.getTamano() != null ? t.getTamano().name() : null,
                t.getDescripcion(),
                t.getFoto(),
                t.getFechaIngreso() != null ? t.getFechaIngreso().toLocalDate() : null
        );
    }
}