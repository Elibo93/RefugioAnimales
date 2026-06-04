package es.refugio.refugio.infraestructure.db.jpa.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "adopciones")
public class AdopcionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adoptante_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AdoptanteEntity adoptante;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AnimalEntity animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_adopcion_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private SolicitudAdopcionEntity solicitudAdopcion;

    @Column(name = "fecha_adopcion", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime fechaAdopcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAdopcion estado;

    @Column(name = "contrato", length = 500)
    private String contrato;
}
