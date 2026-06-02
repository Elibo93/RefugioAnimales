package es.refugio.refugio.infraestructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.web.dto.animal.AnimalRequest;
import es.refugio.refugio.infraestructure.web.dto.animal.AnimalResponse;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

    CreateAnimalCommand toCommand(AnimalRequest req);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapAnimalId")
    EditAnimalCommand toCommand(int id, AnimalRequest req);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "conteoSolicitudes", ignore = true)
    AnimalResponse toResponse(Animal a);

    @Mapping(target = "id", source = "a.id.value")
    @Mapping(target = "conteoSolicitudes", source = "conteoSolicitudes")
    AnimalResponse toResponse(Animal a, Integer conteoSolicitudes);

    @Mapping(target = "id", source = "id.value")
    AnimalEntity toEntity(Animal a);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapAnimalId")
    Animal toDomain(AnimalEntity e);

    List<Animal> toDomain(List<AnimalEntity> lista);

    @Named("mapAnimalId")
    default AnimalId mapAnimalId(Integer id) {
        return id != null ? new AnimalId(id) : null;
    }
}