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
                request.notas());
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
                request.notas());
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
                animal.getCreatedAt());
    }

    public static AnimalEntity toEntity(Animal t) {
        return AnimalEntity.builder()
                .id(t.getId() != null ? t.getId().getValue() : null)
                .nombre(t.getNombre())
                .especie(t.getEspecie())
                .raza(t.getRaza())
                .sexo(t.getSexo())
                .chipId(t.getChipId())
                .estado(t.getEstado())
                .createdAt(t.getCreatedAt())
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
                .estado(e.getEstado())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public static List<Animal> toDomain(List<AnimalEntity> lista) {
        return lista.stream()
                .map(AnimalMapper::toDomain)
                .collect(Collectors.toList());
    }
}
