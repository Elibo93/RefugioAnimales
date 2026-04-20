package es.refugio.infraestructure.db.repository.mock.persona;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import es.refugio.refugio.infraestructure.db.repository.mock.usuario.UsuarioFactory;
import es.refugio.refugio.infraestructure.db.repository.mock.usuario.UsuarioRepositoryMockImpl;

public class PersonaRepositoryMockImplTest {

    UsuarioRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UsuarioRepositoryMockImpl(); // inicializas el mock
    }

    @Test
    void save() {
        var persona = UsuarioFactory.create();

        Usuario a = repository.save(persona);

        assertAll(
                () -> assertNotNull(a),
                () -> assertNotNull(a.getId()),
                () -> assertNotNull(repository.getById(a.getId())));
    }

    @Test
    void getAll() {
        var personas = repository.getAll();

        assertAll(
                () -> assertNotNull(personas),
                () -> assertEquals(UsuarioFactory.getDemoData().size(), personas.size()));
    }
}
