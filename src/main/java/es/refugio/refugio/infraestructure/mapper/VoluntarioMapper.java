package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioRequest;
import es.refugio.refugio.infraestructure.web.dto.voluntario.VoluntarioResponse;

public class VoluntarioMapper {

    public static CreateVoluntarioCommand toCommand(VoluntarioRequest VoluntarioRequest) {
        return new CreateVoluntarioCommand(VoluntarioRequest.nombre(), VoluntarioRequest.apellido(),
                VoluntarioRequest.email(),
                VoluntarioRequest.telefono(), VoluntarioRequest.especialidad());
    }

    public static VoluntarioResponse toResponse(Voluntario voluntario) {
        return new VoluntarioResponse(
                voluntario.getId(),
                voluntario.getNombre(),
                voluntario.getApellido(),
                voluntario.getEspecialidad(),
                voluntario.getCreatedAt());
    }

    public static EditVoluntarioCommand toCommand(int id, VoluntarioRequest VoluntarioRequest) {
        return new EditVoluntarioCommand(new VoluntarioId(id), VoluntarioRequest.especialidad(),
                VoluntarioRequest.email(),
                VoluntarioRequest.telefono());
    }

    public static VoluntarioEntity toEntity(Voluntario a) {
        return VoluntarioEntity.builder()
                .id(a.getId() != null ? a.getId().getValue() : null)
                .nombre(a.getNombre())
                .apellido(a.getApellido())
                .especialidad(a.getEspecialidad())
                .email(a.getEmail())
                .telefono(a.getTelefono())
                .createdAt(a.getCreatedAt())
                .build();
    }

    public static Voluntario toDomain(VoluntarioEntity e) {
        return Voluntario.builder()
                .id(e.getId() != null ? new VoluntarioId(e.getId()) : null)
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .especialidad(e.getEspecialidad())
                .email(e.getEmail())
                .telefono(e.getTelefono())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public static List<Voluntario> toDomain(List<VoluntarioEntity> lista) {
        return lista.stream()
                .map(VoluntarioMapper::toDomain)
                .collect(Collectors.toList());
    }
}
