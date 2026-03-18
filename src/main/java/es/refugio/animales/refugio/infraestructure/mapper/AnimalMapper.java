package es.refugio.animales.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.animales.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.animales.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.animales.refugio.domain.model.animal.Animal;
import es.refugio.animales.refugio.domain.model.animal.AnimalId;
import es.refugio.animales.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.animales.refugio.infraestructure.web.dto.animal.AnimalRequest;
import es.refugio.animales.refugio.infraestructure.web.dto.animal.AnimalResponse;

public class AnimalMapper {

    public static CreateAnimalCommand toCommand(AnimalRequest request) {
        return new CreateAnimalCommand(
                request.nombre(),
                request.especie(),
                request.raza(),
                request.sexo(),
                request.chipId(),
                request.estado(),
                request.edad(),
                request.tamano(),
                request.descripcion(),
                request.foto());
    }

    public static EditAnimalCommand toEditCommand(AnimalId id, AnimalRequest request) {
        return new EditAnimalCommand(
                id,
                request.nombre(),
                request.especie(),
                request.raza(),
                request.sexo(),
                request.chipId(),
                request.estado(),
                request.edad(),
                request.tamano(),
                request.descripcion(),
                request.foto());
    }

    public static AnimalResponse toResponse(Animal animal) {
        return new AnimalResponse(
                animal.getId() != null ? animal.getId().getValue() : 0,
                animal.getNombre(),
                animal.getEspecie(),
                animal.getRaza(),
                animal.getSexo(),
                animal.getChipId(),
                animal.getEstado(),
                animal.getEdad(),
                animal.getTamano(),
                animal.getDescripcion(),
                animal.getFoto(),
                animal.getFechaIngreso());
    }

    public static AnimalEntity toEntity(Animal t) {
        return AnimalEntity.builder()
                .id(t.getId() != null ? t.getId().getValue() : null)
                .nombre(t.getNombre())
                .especie(t.getEspecie())
                .raza(t.getRaza())
                .sexo(t.getSexo())
                .chipId(t.getChipId())
                .edad(t.getEdad())
                .tamano(t.getTamano())
                .descripcion(t.getDescripcion())
                .foto(t.getFoto())
                .fechaIngreso(t.getFechaIngreso())
                .build();
    }

    public static Animal toDomain(AnimalEntity e) {
        return Animal.builder()
                .id(e.getId() != null ? new AnimalId(e.getId()) : null)
                .nombre(e.getNombre())
                .especie(e.getEspecie())
                .raza(e.getRaza())
                .sexo(e.getSexo())
                .chipId(e.getChipId())
                .edad(e.getEdad())
                .tamano(e.getTamano())
                .descripcion(e.getDescripcion())
                .foto(e.getFoto())
                .fechaIngreso(e.getFechaIngreso())
                .build();
    }

    public static List<Animal> toDomain(List<AnimalEntity> lista) {
        return lista.stream()
                .map(AnimalMapper::toDomain)
                .collect(Collectors.toList());
    }
}
