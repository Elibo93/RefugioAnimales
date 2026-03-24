package es.refugio.refugio.infraestructure.db.jpa.entity;

import java.time.LocalDateTime;
import java.util.List;

import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.animal.enums.Sexo;
import es.refugio.refugio.domain.model.animal.enums.Tamano;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "animales")
public class AnimalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false)
    private Especie especie;

    @Column(name = "especie_personalizada")
    private String especiePersonalizada;

    @Column(name = "raza", nullable = false)
    private String raza;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false)
    private Sexo sexo;

    @Column(name = "chip_id", nullable = false, unique = true)
    private String chipId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAnimal estado;

    @Column(name = "edad")
    private Integer edad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tamano")
    private Tamano tamano;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "foto")
    private String foto;
    
    @Column(name = "peso")
    private Double peso;

    @Column(name = "nivel_energia")
    private Integer nivelEnergia;

    @Column(name = "urgencia", columnDefinition = "TINYINT(1)")
    private Boolean urgencia;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @OneToOne(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private AdopcionEntity adopcion;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialMedicoEntity> historialMedico;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitudAdopcionEntity> solicitudesAdopcion;
}