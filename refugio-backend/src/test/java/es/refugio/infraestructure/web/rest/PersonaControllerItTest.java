package es.refugio.infraestructure.web.rest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.refugio.refugio.infraestructure.web.rest.UsuarioController;

@SpringBootTest
public class PersonaControllerItTest {

    @Autowired
    private UsuarioController controller;

    @Test
    public void When_Allpersonas_Expect_Lista() {

        int numpersonas = 2; // Matching data.sql

        var lista = controller.getAll();

        assertAll(
                () -> assertNotNull(lista),
                () -> assertEquals(numpersonas, lista.size()));
    }
}
