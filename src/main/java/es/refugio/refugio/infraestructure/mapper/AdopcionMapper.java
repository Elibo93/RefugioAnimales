package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdopcionEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionResponse;

public class AdopcionMapper {

    public static CreateAdopcionCommand toCommand(AdopcionRequest AdopcionRequest) {
        return new CreateAdopcionCommand(new UsuarioId(AdopcionRequest.PersonaId()),
                new AnimalId(AdopcionRequest.AnimalId()));
    }

    public static AdopcionResponse toResponse(Adopcion adopcion) {
        return new AdopcionResponse(
                adopcion.getId() != null ? adopcion.getId().getValue() : 0,
                adopcion.getUsuarioId() != null ? adopcion.getUsuarioId().getValue() : 0,
                adopcion.getAnimalId() != null ? adopcion.getAnimalId().getValue() : 0,
                adopcion.getCreatedAt());
    }

    public static EditAdopcionCommand toCommand(int id, AdopcionRequest AdopcionRequest) {
        return new EditAdopcionCommand(new AdopcionId(id), new UsuarioId(AdopcionRequest.PersonaId()),
                new AnimalId(AdopcionRequest.AnimalId()));
    }

    public static AdopcionEntity toEntity(Adopcion i) {

        UsuarioEntity Persona = new UsuarioEntity();
        Persona.setId(i.getUsuarioId().getValue());

        AnimalEntity Animal = new AnimalEntity();
        Animal.setId(i.getAnimalId().getValue());

        AdopcionId id = i.getId();
        return AdopcionEntity.builder()
                .id(id != null ? id.getValue() : null)
                .persona(Persona)
                .animal(Animal)
                .createdAt(i.getCreatedAt())
                .build();
    }

    public static Adopcion toDomain(AdopcionEntity e) {
        return Adopcion.builder()
                .id(e.getId() != null ? new AdopcionId(e.getId()) : null)
                .usuarioId(new UsuarioId(e.getPersona().getId()))
                .animalId(new AnimalId(e.getAnimal().getId()))
                .createdAt(e.getCreatedAt())
                .build();
    }

    public static List<Adopcion> toDomain(List<AdopcionEntity> lista) {
        return lista.stream()
                .map(AdopcionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
