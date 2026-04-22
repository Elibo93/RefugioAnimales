package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.application.command.adoptante.EditAdoptanteCommand;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteRequest;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteResponse;
import es.refugio.refugio.infraestructure.web.dto.adoptante.AdoptanteUpdateRequest;

public class AdoptanteMapper {

    public static CreateAdoptanteCommand toCommand(AdoptanteRequest request) {
        return new CreateAdoptanteCommand(
                request.usuarioId(),
                request.dni(),
                request.direccion(),
                request.fechaNacimiento());
    }

    public static EditAdoptanteCommand toEditCommand(AdoptanteId id, AdoptanteRequest request) {
        return new EditAdoptanteCommand(
                id,
                request.dni(),
                request.direccion(),
                request.fechaNacimiento(),
                request.estadoValidacion());
    }

    public static EditAdoptanteCommand toEditCommand(AdoptanteId id, AdoptanteUpdateRequest request) {
        return new EditAdoptanteCommand(
                id,
                request.dni(),
                request.direccion(),
                request.fechaNacimiento(),
                request.estadoValidacion());
    }

    public static AdoptanteResponse toResponse(Adoptante adoptante) {
        return new AdoptanteResponse(
                adoptante.getId() != null ? adoptante.getId().getValue() : 0,
                adoptante.getUsuarioId(),
                adoptante.getDni(),
                adoptante.getDireccion(),
                adoptante.getFechaNacimiento(),
                adoptante.getEstadoValidacion() != null ? adoptante.getEstadoValidacion().name() : null,
                adoptante.getFechaRegistro());
    }

    public static AdoptanteEntity toEntity(Adoptante t) {
        return AdoptanteEntity.builder()
                .id(t.getId() != null ? t.getId().getValue() : null)
                .dni(t.getDni())
                .direccion(t.getDireccion())
                .fechaNacimiento(t.getFechaNacimiento())
                .estadoValidacion(t.getEstadoValidacion() != null ? t.getEstadoValidacion().name() : null)
                .fechaRegistro(t.getFechaRegistro())
                .usuarioId(t.getUsuarioId())
                .build();
    }

    public static Adoptante toDomain(AdoptanteEntity e) {
        return Adoptante.builder()
                .id(e.getId() != null ? new AdoptanteId(e.getId()) : null)
                .usuarioId(e.getUsuarioId())
                .nombre("")
                .apellido("")
                .dni(e.getDni())
                .direccion(e.getDireccion())
                .fechaNacimiento(e.getFechaNacimiento())
                .estadoValidacion(e.getEstadoValidacion() != null
                        ? EstadoValidacion.valueOf(e.getEstadoValidacion().toUpperCase())
                        : null)
                .fechaRegistro(e.getFechaRegistro())
                .build();
    }

    public static List<Adoptante> toDomain(List<AdoptanteEntity> lista) {
        return lista.stream()
                .map(AdoptanteMapper::toDomain)
                .collect(Collectors.toList());
    }
}