package es.refugio.animales.refugio.infraestructure.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.refugio.animales.refugio.application.command.usuario.CreatePersonaCommand;
import es.refugio.animales.refugio.application.command.usuario.EditPersonaCommand;
import es.refugio.animales.refugio.application.service.persona.CreatePersonaService;
import es.refugio.animales.refugio.application.service.persona.DeletePersonaService;
import es.refugio.animales.refugio.application.service.persona.EditPersonaService;
import es.refugio.animales.refugio.application.service.persona.FindPersonaService;
import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.domain.model.usuario.PersonaId;
import es.refugio.animales.refugio.infraestructure.mapper.PersonaMapper;
import es.refugio.animales.refugio.infraestructure.web.dto.persona.PersonaRequest;
import es.refugio.animales.refugio.infraestructure.web.dto.persona.PersonaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/personas")
@RequiredArgsConstructor
public class PersonaController {
    private final CreatePersonaService createPersonaService;
    private final FindPersonaService findPersonaService;
    private final DeletePersonaService deletePersonaService;
    private final EditPersonaService editPersonaService;

    @Operation(summary = "Crea un Persona", description = "Crea un Persona dados sus datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Persona creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos introducidos inválidos")
    })
    @PostMapping
    public ResponseEntity<PersonaResponse> createPersona(
            @jakarta.validation.Valid @RequestBody PersonaRequest PersonaRequest) {
        CreatePersonaCommand comando = PersonaMapper.toCommand(PersonaRequest);
        Persona persona = createPersonaService.createPersona(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(PersonaMapper.toResponse(persona));
    }

    @Operation(summary = "Obtiene el listado de personas", description = "Busca en la base de datos todos los personas y sus detalles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de personas generado"),
            @ApiResponse(responseCode = "404", description = "No hay personas en la base de datos")
    })
    @GetMapping
    public List<PersonaResponse> getAll() {

        return findPersonaService.findAll()
                .stream()
                .map(PersonaMapper::toResponse)
                .toList();

    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponse> findPersonaById(@PathVariable int id) {
        Persona persona = findPersonaService.findById(new PersonaId(id));
        return ResponseEntity.ok(PersonaMapper.toResponse(persona));
    }

    @Operation(summary = "Elimina un Persona", description = "Elimina un Persona de la base de datos dado un id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sin cuerpo, Persona eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersona(@PathVariable int id) {
        deletePersonaService.delete(new PersonaId(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Edita un Persona", description = "Edita los datos de un Persona dado su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona editado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos introducidos inválidos")
    })
    @PutMapping("/{id}")
    public PersonaResponse editPersona(@PathVariable int id,
            @jakarta.validation.Valid @RequestBody PersonaRequest PersonaRequest) {
        EditPersonaCommand comando = PersonaMapper.toCommand(id, PersonaRequest);
        Persona persona = editPersonaService.update(comando);
        return PersonaMapper.toResponse(persona);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
