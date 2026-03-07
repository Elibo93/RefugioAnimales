package es.refugio.animales.refugio.infraestructure.db.repository.mock.persona;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.refugio.animales.refugio.domain.model.persona.Persona;
import es.refugio.animales.refugio.domain.repository.PersonaRepository;

public class PersonaRepositoryMockImplTest {

    PersonaRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PersonaRepositoryMockImpl(); // inicializas el mock
    }

    @Test
    void save() {
        var persona = PersonaFactory.create();

        Persona a = repository.save(persona);

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
                () -> assertEquals(PersonaFactory.getDemoData().size(), personas.size()));
    }
}
