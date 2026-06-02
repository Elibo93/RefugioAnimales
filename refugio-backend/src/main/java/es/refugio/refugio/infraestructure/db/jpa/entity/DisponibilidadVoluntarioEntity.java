package es.refugio.refugio.infraestructure.db.jpa.entity;

import es.refugio.refugio.domain.model.voluntario.enums.EstadoDisponibilidad;
import es.refugio.refugio.domain.model.voluntario.enums.TurnoDisponibilidad;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "disponibilidad_voluntario")
public class DisponibilidadVoluntarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voluntario_id", nullable = false)
    private VoluntarioEntity voluntario;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false, length = 20)
    private TurnoDisponibilidad turno;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoDisponibilidad estado;
}
