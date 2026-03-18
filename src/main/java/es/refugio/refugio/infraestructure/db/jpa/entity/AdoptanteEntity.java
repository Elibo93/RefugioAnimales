package es.refugio.refugio.infraestructure.db.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "adoptantes")
public class AdoptanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dni", nullable = false, unique = true, length = 20)
    private String dni;

    @Column(name = "direccion", nullable = false, length = 500)
    private String direccion;

    @Column(name = "estado_validacion", nullable = false, length = 50)
    private String estadoValidacion; // "pendiente", "aprobado", "rechazado"

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // --- RELACIONES ---

    /**
     * Relación con Usuario. 
     * Usamos @ManyToOne porque un Usuario puede tener el rol de Adoptante.
     * Si un usuario solo puede tener UN perfil de adoptante, podrías usar @OneToOne.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    /**
     * Relación 1 Adoptante -> N Solicitudes.
     * El campo 'adoptante' debe existir en SolicitudAdopcionEntity.
     */
    @OneToMany(mappedBy = "adoptante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitudAdopcionEntity> solicitudes;

    /**
     * Relación 1 Adoptante -> N Adopciones.
     * El campo 'adoptante' debe existir en AdopcionEntity.
     */
    @OneToMany(mappedBy = "adoptante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdopcionEntity> adopciones;

    // --- MÉTODOS DE CICLO DE VIDA ---

    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.estadoValidacion == null) {
            this.estadoValidacion = "pendiente";
        }
    }
}