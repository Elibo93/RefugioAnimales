package es.refugio.refugio.infraestructure.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;

class AdoptanteMapperTest {

    @Test
    void toDomain_shouldMapNombreAndApellidoFromUsuario() {
        // Arrange
        UsuarioEntity usuario = UsuarioEntity.builder()
                .id(1)
                .nombre("Juan")
                .apellido("Pérez")
                .build();
        
        AdoptanteEntity entity = AdoptanteEntity.builder()
                .id(10)
                .usuario(usuario)
                .dni("12345678Z")
                .direccion("Calle Falsa 123")
                .build();

        // Act
        Adoptante domain = AdoptanteMapper.toDomain(entity);

        // Assert
        assertEquals("Juan", domain.getNombre());
        assertEquals("Pérez", domain.getApellido());
        assertEquals("Calle Falsa 123", domain.getDireccion());
    }
}
