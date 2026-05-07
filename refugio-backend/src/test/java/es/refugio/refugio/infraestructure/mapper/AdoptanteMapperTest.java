package es.refugio.refugio.infraestructure.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;

class AdoptanteMapperTest {

    @Test
    void toDomain_shouldMapEntityToDomain() {
        // Arrange
        AdoptanteEntity entity = AdoptanteEntity.builder()
                .id(10)
                .usuarioId(1)
                .build();

        // Act
        Adoptante domain = AdoptanteMapper.toDomain(entity);

        // Assert
        assertEquals(10, domain.getId().getValue());
        assertEquals(1, domain.getUsuarioId());
    }
}
