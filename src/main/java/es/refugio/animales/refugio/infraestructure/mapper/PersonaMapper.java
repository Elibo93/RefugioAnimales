package es.refugio.animales.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.animales.refugio.application.command.persona.CreatePersonaCommand;
import es.refugio.animales.refugio.application.command.persona.EditPersonaCommand;
import es.refugio.animales.refugio.domain.model.persona.Persona;
import es.refugio.animales.refugio.domain.model.persona.PersonaId;
import es.refugio.animales.refugio.infraestructure.db.jpa.entity.PersonaEntity;
import es.refugio.animales.refugio.infraestructure.web.dto.persona.PersonaRequest;
import es.refugio.animales.refugio.infraestructure.web.dto.persona.PersonaResponse;

public class PersonaMapper {

    public static CreatePersonaCommand toCommand(PersonaRequest req) {
        return new CreatePersonaCommand(
                req.dni(),
                req.nombre(),
                req.apellido(),
                req.email(),
                req.telefono(),
                req.direccion(),
                req.fechaNacimiento()

        );
    }

    public static EditPersonaCommand toCommand(int id, PersonaRequest req) {
        return new EditPersonaCommand(
                new PersonaId(id),
                req.email(),
                req.telefono(),
                req.direccion()

        );
    }

    public static PersonaResponse toResponse(Persona persona) {
        return new PersonaResponse(
                persona.getId() != null ? persona.getId().getValue() : 0,
                persona.getDni(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getEmail(),
                persona.getTelefono(),
                persona.getDireccion(),
                persona.getFechaNacimiento(),
                persona.getCreatedAt());
    }

    public static PersonaEntity toEntity(Persona a) {
        return PersonaEntity.builder()
                .id(a.getId() != null ? a.getId().getValue() : null)
                .dni(a.getDni())
                .nombre(a.getNombre())
                .apellido(a.getApellido())
                .email(a.getEmail())
                .telefono(a.getTelefono())
                .direccion(a.getDireccion())
                .fechaNacimiento(a.getFechaNacimiento())
                .createdAt(a.getCreatedAt())
                .build();
    }

    public static Persona toDomain(PersonaEntity e) {
        return Persona.builder()
                .id(e.getId() != null ? new PersonaId(e.getId()) : null)
                .dni(e.getDni())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .email(e.getEmail())
                .telefono(e.getTelefono())
                .direccion(e.getDireccion())
                .fechaNacimiento(e.getFechaNacimiento())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public static List<Persona> toDomain(List<PersonaEntity> lista) {
        return lista.stream()
                .map(PersonaMapper::toDomain)
                .collect(Collectors.toList());
    }
}
