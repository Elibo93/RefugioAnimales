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
    private String email;
    private String username;
    private String contrasena;
    private Rol rol;
    private LocalDateTime createdAt;

}