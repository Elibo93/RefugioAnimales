package es.refugio.refugio.infraestructure.db.jpa.entity;

import java.time.LocalDateTime;
import java.util.List;

import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.Sexo;
import es.refugio.refugio.domain.model.animal.enums.Tamano;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "preferencias_adopcion")
public class PreferenciaAdopcionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Integer usuarioId;

    @ElementCollection(targetClass = Especie.class)
    @CollectionTable(name = "preferencias_especies", joinColumns = @JoinColumn(name = "preferencia_id"))
    @Column(name = "especie")
    @Enumerated(EnumType.STRING)
    private List<Especie> especies;

    @ElementCollection(targetClass = Tamano.class)
    @CollectionTable(name = "preferencias_tamanos", joinColumns = @JoinColumn(name = "preferencia_id"))
    @Column(name = "tamano")
    @Enumerated(EnumType.STRING)
    private List<Tamano> tamanos;

    @ElementCollection(targetClass = Sexo.class)
    @CollectionTable(name = "preferencias_sexos", joinColumns = @JoinColumn(name = "preferencia_id"))
    @Column(name = "sexo")
    @Enumerated(EnumType.STRING)
    private List<Sexo> sexos;

    @Column(name = "edad_max")
    private Integer edadMax;

    @Column(name = "nivel_energia_max")
    private Integer nivelEnergiaMax;

    @Column(name = "notificaciones_activas", columnDefinition = "BIT")
    private Boolean notificacionesActivas;

    @Column(name = "encuesta_omitida", columnDefinition = "BIT")
    private Boolean encuestaOmitida;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
