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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios y cuentas del sistema")
public class UsuarioController {

    private final CreateUsuarioService createUsuarioService;
    private final FindUsuarioService findUsuarioService;
    private final DeleteUsuarioService deleteUsuarioService;
    private final EditUsuarioService editUsuarioService;

    @Operation(summary = "Crea un Usuario", description = "Registra un nuevo usuario con sus credenciales y rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(
            @jakarta.validation.Valid @RequestBody UsuarioRequest usuarioRequest) {
        CreateUsuarioCommand comando = UsuarioMapper.toCommand(usuarioRequest);
        Usuario usuario = createUsuarioService.createUsuario(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponse(usuario));
    }

    @Operation(summary = "Obtiene el listado de usuarios", description = "Retorna todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado generado con éxito"),
            @ApiResponse(responseCode = "404", description = "No existen usuarios registrados")
    })
    @GetMapping
    public List<UsuarioResponse> getAll() {
        return findUsuarioService.findAll()
                .stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findUsuarioById(@PathVariable int id) {
        Usuario usuario = findUsuarioService.findById(new UsuarioId(id));
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    @Operation(summary = "Elimina un Usuario", description = "Elimina de forma permanente la cuenta de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable int id) {
        deleteUsuarioService.delete(new UsuarioId(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Edita un Usuario", description = "Actualiza los datos básicos o el rol de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos")
    })
    @PutMapping("/{id}")
    public UsuarioResponse editUsuario(@PathVariable int id,
            @jakarta.validation.Valid @RequestBody UsuarioRequest usuarioRequest) {
        EditUsuarioCommand comando = UsuarioMapper.toCommand(id, usuarioRequest);
        Usuario usuario = editUsuarioService.update(comando);
        return UsuarioMapper.toResponse(usuario);
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