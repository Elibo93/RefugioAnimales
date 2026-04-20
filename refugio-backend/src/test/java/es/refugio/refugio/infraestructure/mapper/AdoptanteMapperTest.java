package es.refugio.refugio.infraestructure.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;


class AdoptanteMapperTest {

    @Test
    void toDomain_shouldMapNombreAndApellidoFromUsuario() {
        // Arrange
        AdoptanteEntity entity = AdoptanteEntity.builder()
                .id(10)
                .usuarioId(1)
                .dni("12345678Z")
                .direccion("Calle Falsa 123")
                .build();

        // Act
        Adoptante domain = AdoptanteMapper.toDomain(entity);

        // Assert
        assertEquals("", domain.getNombre());
        assertEquals("", domain.getApellido());
        assertEquals("Calle Falsa 123", domain.getDireccion());
    }
}
