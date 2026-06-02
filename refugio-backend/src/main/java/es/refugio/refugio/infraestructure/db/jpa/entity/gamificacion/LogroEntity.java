package es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion;

import es.refugio.refugio.domain.model.gamificacion.enums.CategoriaLogro;
import es.refugio.refugio.domain.model.gamificacion.enums.RarezaLogro;
import es.refugio.refugio.domain.model.gamificacion.enums.RequisitoTipo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "gamificacion_logro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaLogro categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "requisito_tipo", nullable = false)
    private RequisitoTipo requisitoTipo;

    @Column(name = "requisito_valor", nullable = false)
    private BigDecimal requisitoValor;

    @Column(name = "icono_lucide")
    private String iconoLucide;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RarezaLogro rareza;
}
