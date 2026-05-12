package es.refugio.refugio.infraestructure.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository usuarioEntityJpaRepository;
    private final es.refugio.auth.infrastructure.security.JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Crea un Usuario", description = "Registra un nuevo usuario con sus credenciales y rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(
            @jakarta.validation.Valid @RequestBody UsuarioRequest usuarioRequest) {
        CreateUsuarioCommand comando = UsuarioMapper.toCommand(usuarioRequest);
        Usuario usuario = createUsuarioService.createUsuario(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponse(usuario));
    }

    @PostMapping("/publico")
    public ResponseEntity<?> createUsuarioPublic(
            @jakarta.validation.Valid @RequestBody UsuarioRequest usuarioRequest) {
        try {
            CreateUsuarioCommand comando = UsuarioMapper.toCommand(usuarioRequest);
            Usuario usuario = createUsuarioService.createUsuario(comando);
            return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponse(usuario));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            String msg = e.getMostSpecificCause().getMessage();
            String userFriendlyMsg = "Error: El nombre de usuario o el email ya están registrados.";
            
            if (msg != null && msg.contains("usuarios.UKm2dvbwfge291euvmk6vkkocao")) {
                 userFriendlyMsg = "El nombre de usuario ya está en uso.";
            } else if (msg != null && msg.contains("email")) {
                 userFriendlyMsg = "El correo electrónico ya está registrado.";
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", userFriendlyMsg));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al registrar el usuario: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.PutMapping("/{id}/rol")
    public ResponseEntity<Map<String, String>> updateRole(@PathVariable int id, @RequestBody Map<String, String> body) {
        var userOptional = usuarioEntityJpaRepository.findById(id);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            try {
                String nuevoRol = body.get("rol");
                user.setRol(es.refugio.auth.domain.Rol.valueOf(nuevoRol));
                usuarioEntityJpaRepository.save(user);

                // Generar nuevo token con el rol actualizado para que el frontend pueda refrescar la sesión
                java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(nuevoRol));
                if ("ROLE_VOLUNTARIO_ADOPTANTE".equals(nuevoRol)) {
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_VOLUNTARIO"));
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADOPTANTE"));
                }
                var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
                String newToken = tokenProvider.generateToken(auth);

                return ResponseEntity.ok(Map.of("token", newToken));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @PostMapping("/{id}/verificar-password")
    public ResponseEntity<?> verifyPassword(@PathVariable int id, @RequestParam String password) {
        System.out.println("[DEBUG] Verificando password para usuario ID: " + id);
        var userOptional = usuarioEntityJpaRepository.findById(id);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            
            if (password != null && passwordEncoder.matches(password.trim(), user.getContrasena())) {
                return ResponseEntity.ok(Map.of("valid", true));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("valid", false, "message", "Contraseña actual incorrecta"));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable int id, @RequestBody Map<String, String> body) {
        var userOptional = usuarioEntityJpaRepository.findById(id);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            String newPassword = body.get("newPassword");
            if (newPassword == null || newPassword.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "La nueva contraseña no puede estar vacía"));
            }
            user.setContrasena(passwordEncoder.encode(newPassword));
            usuarioEntityJpaRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtiene el listado de usuarios", description = "Retorna todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado generado con éxito"),
            @ApiResponse(responseCode = "404", description = "No existen usuarios registrados")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UsuarioResponse> getAll() {
        return findUsuarioService.findAll()
                .stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @PutMapping("/{id}")
    public UsuarioResponse editUsuario(@PathVariable int id,
            @jakarta.validation.Valid @RequestBody UsuarioRequest usuarioRequest) {
        
        // SEGURIDAD EXTRA: Si no es ADMIN, forzar que el rol sea el actual (no permitir escalada)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        es.refugio.auth.domain.Rol finalRol = usuarioRequest.rol();
        if (!isAdmin) {
            Usuario usuarioActual = findUsuarioService.findById(new UsuarioId(id));
            finalRol = usuarioActual.getRol();
        }

        EditUsuarioCommand comando = new EditUsuarioCommand(
            new UsuarioId(id), 
            usuarioRequest.email(), 
            usuarioRequest.username(), 
            finalRol
        );
        
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