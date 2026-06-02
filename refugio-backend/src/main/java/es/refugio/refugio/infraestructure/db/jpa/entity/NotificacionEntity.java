package es.refugio.refugio.infraestructure.db.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = true)
    private Integer usuarioId;

    @Column(length = 50)
    private String rol; // Opcional: Para notificaciones por rol (e.g. ROLE_ADMIN)

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fecha;

    @Column(nullable = false)
    private boolean leida;

    @Column(length = 50)
    private String tipo; // E.g. ADOPCION, TAREA, SISTEMA

    private String enlace; // URL opcional

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
