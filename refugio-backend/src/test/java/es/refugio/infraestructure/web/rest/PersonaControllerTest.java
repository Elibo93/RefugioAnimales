package es.refugio.infraestructure.web.rest;

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

import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.infraestructure.db.repository.mock.usuario.UsuarioFactory;
import es.refugio.refugio.infraestructure.web.dto.usuario.UsuarioRequest;
import es.refugio.refugio.infraestructure.web.dto.usuario.UsuarioResponse;

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
        private JacksonTester<UsuarioRequest> jsonPersonaRequest;

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

                List<UsuarioResponse> res = mapper.readValue(response.getContentAsString(),
                                mapper.getTypeFactory().constructCollectionType(List.class, UsuarioResponse.class));

                assertAll(
                                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                                () -> assertTrue(res.size() == numpersonas));
        }

        @Test
        @Order(10)
        public void When_Post_CreatePersona() throws Exception {
                Usuario nuevo = UsuarioFactory.create();

                UsuarioRequest req = new UsuarioRequest(nuevo);

                MockHttpServletResponse response = mockMvc.perform(
                                post(ENDPOINT)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                // Le paso el body
                                                .content(jsonPersonaRequest.write(req).getJson())
                                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                UsuarioResponse res = mapper.readValue(response.getContentAsString(), UsuarioResponse.class);

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
                Usuario nuevo = UsuarioFactory.create();

                nuevo.setNombre(null);

                UsuarioRequest req = new UsuarioRequest(nuevo);

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

                Usuario editado = UsuarioFactory.create();
                editado.setId(new UsuarioId(1)); // Indicamos que editamos el Persona 1

                UsuarioRequest req = new UsuarioRequest(editado);

                MockHttpServletResponse response = mockMvc.perform(
                                put(ENDPOINT + "/" + editado.getId().getValue())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                // Le paso el body
                                                .content(jsonPersonaRequest.write(req).getJson())
                                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                UsuarioResponse res = mapper.readValue(response.getContentAsString(), UsuarioResponse.class);

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

                Usuario nuevo = UsuarioFactory.create();
                nuevo.setId(new UsuarioId(1));

                UsuarioRequest req = new UsuarioRequest(nuevo);

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
