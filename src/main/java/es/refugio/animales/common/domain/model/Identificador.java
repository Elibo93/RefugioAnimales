package es.refugio.animales.common.domain.model;

import lombok.Data;

@Data
public abstract class Identificador {
    private final Integer value;

    protected Identificador(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("El identificador no puede ser nulo");
        }
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}




