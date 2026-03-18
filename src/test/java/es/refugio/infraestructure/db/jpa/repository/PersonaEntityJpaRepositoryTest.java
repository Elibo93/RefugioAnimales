package es.refugio.infraestructure.db.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.repository.mock.usuario.UsuarioFactory;
import es.refugio.refugio.infraestructure.mapper.UsuarioMapper;

@DataJpaTest(showSql = true)

public class PersonaEntityJpaRepositoryTest {

    @Autowired

    private UsuarioEntityJpaRepository repository;

    @Test
    @Order(1)

    void findAll() {

        var personas = repository.findAll();

        assertAll(
                () -> assertNotNull(personas),
                () -> assertTrue(!personas.isEmpty()));
    }

    @Test
    @Order(2)
    void findById() {
        var entity = repository.findById(1).get();

        assertAll(
                () -> assertNotNull(entity),
                () -> assertEquals(1, entity.getId()),
                () -> assertEquals("Diego", entity.getNombre()));
    }

    @Test
    @Order(3)
    void findByName() {
        var entity = repository.findByNombre("Diego");

        assertAll(
                () -> assertNotNull(entity),
                () -> assertEquals(1, entity.getId()),
                () -> assertEquals("Diego", entity.getNombre()));
    }

    @Test
    @Order(5)
    void create() {
        Usuario p = UsuarioFactory.create();
        var nuevo = UsuarioMapper.toEntity(p);
        nuevo.setId(null);
        var entity = repository.save(nuevo);

        assertAll(
                () -> assertNotNull(entity),
                () -> assertTrue(entity.getId() != null),

                () -> assertEquals(nuevo.getDni(), entity.getDni()),
                () -> assertEquals(nuevo.getNombre(), entity.getNombre()),
                () -> assertEquals(nuevo.getApellido(), entity.getApellido()),
                () -> assertEquals(nuevo.getEmail(), entity.getEmail()),
                () -> assertEquals(nuevo.getTelefono(), entity.getTelefono()),
                () -> assertEquals(nuevo.getDireccion(), entity.getDireccion()),
                () -> assertEquals(nuevo.getFechaNacimiento(), entity.getFechaNacimiento()),
                () -> assertEquals(nuevo.getCreatedAt(), entity.getCreatedAt()));
    }

    @Test
    @Order(10)
    void update() {
        var entity = repository.findById(1).get();
        entity.setNombre("NombreModificado");
        var actualizado = repository.save(entity);

        assertAll(
                () -> assertNotNull(actualizado),
                () -> assertEquals(1, actualizado.getId()),
                () -> assertEquals("NombreModificado", actualizado.getNombre()));
    }

    @Test
    @Order(15)
    void delete() {
        var entity = repository.findById(1);
        repository.delete(entity.get());

        var eliminado = repository.findById(1).isEmpty();
        assertAll(
                () -> assertTrue(eliminado));
    }

}
