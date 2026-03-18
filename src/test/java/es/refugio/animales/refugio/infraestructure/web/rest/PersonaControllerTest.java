package es.refugio.animales.refugio.infraestructure.web.rest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.domain.model.usuario.PersonaId;
import es.refugio.animales.refugio.infraestructure.db.repository.mock.persona.PersonaFactory;
import es.refugio.animales.refugio.infraestructure.web.dto.persona.PersonaRequest;
import es.refugio.animales.refugio.infraestructure.web.dto.persona.PersonaResponse;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PersonaControllerTest {

        public static String ENDPOINT = "/api/v1/personas";

        // Json
        private ObjectMapper mapper = new ObjectMapper();

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private JacksonTester<PersonaRequest> jsonPersonaRequest;

        @BeforeEach
        public void setUp() {
                mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
        }

        @Test
        @Order(1)
        public void When_Get_getAll_Expect_Lista() throws Exception {

                int numpersonas = 2; // Matching data.sql

                MockHttpServletResponse response = mockMvc.perform(
                                get(ENDPOINT).accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                List<PersonaResponse> res = mapper.readValue(response.getContentAsString(),
                                mapper.getTypeFactory().constructCollectionType(List.class, PersonaResponse.class));

                assertAll(
                                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                                () -> assertTrue(res.size() == numpersonas));
        }

        @Test
        @Order(10)
        public void When_Post_CreatePersona() throws Exception {
                Persona nuevo = PersonaFactory.create();

                PersonaRequest req = new PersonaRequest(nuevo);

                MockHttpServletResponse response = mockMvc.perform(
                                post(ENDPOINT)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                // Le paso el body
                                                .content(jsonPersonaRequest.write(req).getJson())
                                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                PersonaResponse res = mapper.readValue(response.getContentAsString(), PersonaResponse.class);

                assertAll(
                                () -> assertEquals(response.getStatus(), HttpStatus.CREATED.value()),
                                () -> assertEquals(res.nombre(), nuevo.getNombre()),
                                () -> assertEquals(res.apellido(), nuevo.getApellido()),
                                () -> assertEquals(res.email(), nuevo.getEmail()),
                                () -> assertTrue(res.id() > 0));
        }

        @Test
        @Order(11)
        public void Error_Validation_When_CreatePersona_EmptyNombre() throws Exception {
                Persona nuevo = PersonaFactory.create();

                nuevo.setNombre(null);

                PersonaRequest req = new PersonaRequest(nuevo);

                MockHttpServletResponse response = mockMvc.perform(
                                post(ENDPOINT)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(jsonPersonaRequest.write(req).getJson())
                                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        }

        @Order(20)
        public void When_Put_EditPersona() throws Exception {

                Persona editado = PersonaFactory.create();
                editado.setId(new PersonaId(1)); // Indicamos que editamos el Persona 1

                PersonaRequest req = new PersonaRequest(editado);

                MockHttpServletResponse response = mockMvc.perform(
                                put(ENDPOINT + "/" + editado.getId().getValue())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                // Le paso el body
                                                .content(jsonPersonaRequest.write(req).getJson())
                                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                PersonaResponse res = mapper.readValue(response.getContentAsString(), PersonaResponse.class);

                assertAll(
                                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                                () -> assertEquals(res.nombre(), req.nombre()),
                                () -> assertEquals(res.apellido(), req.apellido()),
                                () -> assertEquals(res.email(), req.email()),
                                () -> assertEquals(res.id(), editado.getId().getValue()));

        }

        @Test
        @Order(30)
        public void When_Delete_DeletePersona() throws Exception {

                Persona nuevo = PersonaFactory.create();
                nuevo.setId(new PersonaId(1));

                PersonaRequest req = new PersonaRequest(nuevo);

                MockHttpServletResponse response = mockMvc.perform(
                                delete(ENDPOINT + "/" + nuevo.getId().getValue())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                // Le paso el body
                                                .content(jsonPersonaRequest.write(req).getJson())
                                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                assertEquals(response.getStatus(), HttpStatus.NO_CONTENT.value());
        }
}
