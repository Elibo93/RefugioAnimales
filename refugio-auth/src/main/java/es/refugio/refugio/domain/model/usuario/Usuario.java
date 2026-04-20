package es.refugio.refugio.domain.model.usuario;

import java.time.LocalDateTime;
import es.refugio.auth.domain.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Usuario {

    private UsuarioId id;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private String telefono;
    private Rol rol;
    private LocalDateTime createdAt;

}