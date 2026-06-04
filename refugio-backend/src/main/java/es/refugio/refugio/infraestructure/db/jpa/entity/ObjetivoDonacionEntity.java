package es.refugio.refugio.infraestructure.db.jpa.entity;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.donacion.enums.EstadoObjetivo;
import es.refugio.refugio.domain.model.donacion.enums.PrioridadObjetivo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "objetivos_donacion")
public class ObjetivoDonacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "monto_objetivo", columnDefinition = "DOUBLE", nullable = false)
    private Double montoObjetivo;

    @Column(name = "monto_recaudado", columnDefinition = "DOUBLE")
    private Double montoRecaudado;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PrioridadObjetivo prioridad;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoObjetivo estado;

    @Column(name = "fecha_inicio", columnDefinition = "DATETIME")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_limite", columnDefinition = "DATETIME")
    private LocalDateTime fechaLimite;

    @Column(length = 50)
    private String icono;
}
