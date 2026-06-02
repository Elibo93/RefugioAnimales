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

    @Column(name = "estado_validacion", nullable = false, length = 50)
    private String estadoValidacion;

    @Column(name = "fecha_registro", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime fechaRegistro;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Integer usuarioId;

    @OneToMany(mappedBy = "adoptante", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<SolicitudAdopcionEntity> solicitudes;

    @OneToMany(mappedBy = "adoptante", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<AdopcionEntity> adopciones;

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