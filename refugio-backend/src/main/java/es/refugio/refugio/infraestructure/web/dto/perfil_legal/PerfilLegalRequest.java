package es.refugio.refugio.infraestructure.web.dto.perfil_legal;

import es.refugio.refugio.infraestructure.web.validation.MinAge;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Datos para crear o actualizar un perfil legal")
public record PerfilLegalRequest(
    @NotNull(message = "El ID de usuario es obligatorio")
    Integer usuarioId,
    
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,
    
    @NotBlank(message = "El apellido es obligatorio")
    String apellido,
    
    @Pattern(regexp = "([0-9]{8}[A-Z])|([XYZ][0-9]{7}[A-Z])", message = "DNI/NIE inválido")
    String dni,
    
    String telefono,
    String direccion,
    
    @MinAge(18)
    String fechaNacimiento
) {}
