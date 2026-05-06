package es.refugio.refugio.infraestructure.db.jpa.entity;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;

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
@Table(name = "donaciones")
public class DonacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoDonacion tipo;

    @Column(nullable = false)
    private Double cantidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FrecuenciaDonacion frecuencia;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime fecha;

    @Column(name = "proxima_fecha_pago", columnDefinition = "DATETIME")
    private LocalDateTime proximaFechaPago;

    @Column(length = 500)
    private String descripcion;
}
