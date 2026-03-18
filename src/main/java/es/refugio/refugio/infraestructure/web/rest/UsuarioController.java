package es.refugio.refugio.infraestructure.web.rest;

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

import es.refugio.refugio.application.command.usuario.CreateUsuarioCommand;
import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.application.service.usuario.CreateUsuarioService;
import es.refugio.refugio.application.service.usuario.DeleteUsuarioService;
import es.refugio.refugio.application.service.usuario.EditUsuarioService;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.infraestructure.mapper.UsuarioMapper;
import es.refugio.refugio.infraestructure.web.dto.usuario.UsuarioRequest;
import es.refugio.refugio.infraestructure.web.dto.usuario.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/personas")
@RequiredArgsConstructor
public class UsuarioController {
    private final CreateUsuarioService createPersonaService;
    private final FindUsuarioService findPersonaService;
    private final DeleteUsuarioService deletePersonaService;
    private final EditUsuarioService editPersonaService;

    @Operation(summary = "Crea un Persona", description = "Crea un Persona dados sus datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Persona creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos introducidos inválidos")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> createPersona(
            @jakarta.validation.Valid @RequestBody UsuarioRequest PersonaRequest) {
        CreateUsuarioCommand comando = UsuarioMapper.toCommand(PersonaRequest);
        Usuario persona = createPersonaService.createPersona(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponse(persona));
    }

    @Operation(summary = "Obtiene el listado de personas", description = "Busca en la base de datos todos los personas y sus detalles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de personas generado"),
            @ApiResponse(responseCode = "404", description = "No hay personas en la base de datos")
    })
    @GetMapping
    public List<UsuarioResponse> getAll() {

        return findPersonaService.findAll()
                .stream()
                .map(UsuarioMapper::toResponse)
                .toList();

    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findPersonaById(@PathVariable int id) {
        Usuario persona = findPersonaService.findById(new UsuarioId(id));
        return ResponseEntity.ok(UsuarioMapper.toResponse(persona));
    }

    @Operation(summary = "Elimina un Persona", description = "Elimina un Persona de la base de datos dado un id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sin cuerpo, Persona eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersona(@PathVariable int id) {
        deletePersonaService.delete(new UsuarioId(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Edita un Persona", description = "Edita los datos de un Persona dado su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona editado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos introducidos inválidos")
    })
    @PutMapping("/{id}")
    public UsuarioResponse editPersona(@PathVariable int id,
            @jakarta.validation.Valid @RequestBody UsuarioRequest PersonaRequest) {
        EditUsuarioCommand comando = UsuarioMapper.toCommand(id, PersonaRequest);
        Usuario persona = editPersonaService.update(comando);
        return UsuarioMapper.toResponse(persona);
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
