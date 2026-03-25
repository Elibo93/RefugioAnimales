package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.web.dto.animal.AnimalRequest;
import es.refugio.refugio.infraestructure.web.dto.animal.AnimalResponse;

public class AnimalMapper {

    public static CreateAnimalCommand toCommand(AnimalRequest req) {
        return new CreateAnimalCommand(
                req.nombre(),
                req.especie(),
                req.especiePersonalizada(),
                req.raza(),
                req.sexo(),
                req.chipId(),
                req.estado(),
                req.edad(),
                req.tamano(),
                req.descripcion(),
                req.foto(),
                req.peso(),
                req.nivelEnergia(),
                req.urgencia());
    }

    public static EditAnimalCommand toCommand(int id, AnimalRequest req) {
        return new EditAnimalCommand(
                new AnimalId(id),
                req.nombre(),
                req.chipId(),
                req.estado(),
                req.edad(),
                req.tamano(),
                req.descripcion(),
                req.foto(),
                req.peso(),
                req.nivelEnergia(),
                req.urgencia());
    }

    public static AnimalResponse toResponse(Animal a) {
        return new AnimalResponse(
                a.getId() != null ? a.getId().getValue() : 0,
                a.getNombre(),
                a.getEspecie() != null ? a.getEspecie().name() : null,
                a.getEspeciePersonalizada(),
                a.getRaza(),
                a.getSexo() != null ? a.getSexo().name() : null,
                a.getChipId(),
                a.getEstado() != null ? a.getEstado().name() : null,
                a.getEdad(),
                a.getTamano() != null ? a.getTamano().name() : null,
                a.getDescripcion(),
                a.getFoto(),
                a.getFechaIngreso(),
                a.getPeso(),
                a.getNivelEnergia(),
                a.getUrgencia());
    }

    public static AnimalEntity toEntity(Animal a) {
        return AnimalEntity.builder()
                .id(a.getId() != null ? a.getId().getValue() : null)
                .nombre(a.getNombre())
                .especie(a.getEspecie())
                .especiePersonalizada(a.getEspeciePersonalizada())
                .raza(a.getRaza())
                .sexo(a.getSexo())
                .chipId(a.getChipId())
                .estado(a.getEstado())
                .edad(a.getEdad())
                .tamano(a.getTamano())
                .descripcion(a.getDescripcion())
                .foto(a.getFoto())
                .peso(a.getPeso())
                .nivelEnergia(a.getNivelEnergia())
                .urgencia(a.getUrgencia())
                .fechaIngreso(a.getFechaIngreso())
                .build();
    }

    public static Animal toDomain(AnimalEntity e) {
        return Animal.builder()
                .id(e.getId() != null ? new AnimalId(e.getId()) : null)
                .nombre(e.getNombre())
                .especie(e.getEspecie())
                .especiePersonalizada(e.getEspeciePersonalizada())
                .raza(e.getRaza())
                .sexo(e.getSexo())
                .chipId(e.getChipId())
                .estado(e.getEstado())
                .edad(e.getEdad())
                .tamano(e.getTamano())
                .descripcion(e.getDescripcion())
                .foto(e.getFoto())
                .peso(e.getPeso())
                .nivelEnergia(e.getNivelEnergia())
                .urgencia(e.getUrgencia())
                .fechaIngreso(e.getFechaIngreso())
                .build();
    }

    public static List<Animal> toDomain(List<AnimalEntity> lista) {
        return lista.stream()
                .map(AnimalMapper::toDomain)
                .collect(Collectors.toList());
    }
}