package es.refugio.refugio.infraestructure.web.dto.perfil_legal;

import es.refugio.refugio.infraestructure.web.validation.MinAge;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import es.refugio.refugio.infraestructure.web.validation.ValidDni;

@Schema(description = "Datos para crear o actualizar un perfil legal")
public record PerfilLegalRequest(
    @NotNull(message = "{error.validation.usuario_id_obligatorio}")
    Integer usuarioId,
    
    @NotBlank(message = "{error.validation.nombre_obligatorio}")
    String nombre,
    
    @NotBlank(message = "{error.validation.apellido_obligatorio}")
    String apellido,
    
    @ValidDni
    String dni,
    
    String telefono,
    String direccion,
    
    @MinAge(18)
    String fechaNacimiento
) {}
