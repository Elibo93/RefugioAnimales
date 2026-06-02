package es.refugio.refugio.domain.model.perfil_legal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PerfilLegal {
    private PerfilLegalId id;
    private Integer usuarioId;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String direccion;
    private String fechaNacimiento;
}
