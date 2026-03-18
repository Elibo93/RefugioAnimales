package es.refugio.refugio.infraestructure.db.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "animales")
public class AnimalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "especie", nullable = false, length = 255)
    private String especie;

    @Column(name = "raza", nullable = false, length = 255)
    private String raza;

    @Column(name = "sexo", nullable = false, length = 255)
    private String sexo;

    @Column(name = "chip_id", nullable = false, length = 255)
    private String chipId;

    @Column(name = "estado", nullable = false, length = 255)
    private String estado;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "tamano", length = 50)
    private String tamano;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "foto", length = 255)
    private String foto;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @OneToOne(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private AdopcionEntity adopcion;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialMedicoEntity> historialMedico;
}
