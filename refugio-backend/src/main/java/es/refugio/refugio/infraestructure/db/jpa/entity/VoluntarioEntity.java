package es.refugio.refugio.infraestructure.db.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "voluntarios")
public class VoluntarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "disponibilidad", nullable = false, length = 500)
    private String disponibilidad;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Integer usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private es.refugio.refugio.domain.model.voluntario.enums.EstadoVoluntario status;

    @ManyToMany(mappedBy = "voluntarios")
    private java.util.List<TareaEntity> tareas;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
