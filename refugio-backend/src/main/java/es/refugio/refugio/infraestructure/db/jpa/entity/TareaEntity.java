package es.refugio.refugio.infraestructure.db.jpa.entity;

import java.time.LocalDateTime;
import java.util.List;

import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tareas")
public class TareaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoTarea estado;

    @Column(name = "fecha_limite", columnDefinition = "DATETIME")
    private LocalDateTime fechaLimite;

    @Column(length = 1000)
    private String instrucciones;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "voluntarios_tareas",
        joinColumns = @JoinColumn(name = "tarea_id"),
        inverseJoinColumns = @JoinColumn(name = "voluntario_id")
    )
    private List<VoluntarioEntity> voluntarios;

    @Column(name = "notificado_vencimiento", columnDefinition = "BIT")
    private Boolean notificadoVencimiento;
}
