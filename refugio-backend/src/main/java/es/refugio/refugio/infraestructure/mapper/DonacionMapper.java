package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.application.command.donacion.EditDonacionCommand;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.DonacionEntity;
import es.refugio.refugio.infraestructure.web.dto.donacion.DonacionRequest;
import es.refugio.refugio.infraestructure.web.dto.donacion.DonacionResponse;

public class DonacionMapper {

    public static CreateDonacionCommand toCommand(DonacionRequest req) {
        return new CreateDonacionCommand(
                req.usuarioId(),
                req.tipo(),
                req.cantidad(),
                req.frecuencia(),
                req.fecha(),
                req.proximaFechaPago(),
                req.descripcion());
    }

    public static EditDonacionCommand toCommand(int id, DonacionRequest req) {
        return new EditDonacionCommand(
                new DonacionId(id),
                req.usuarioId(),
                req.tipo(),
                req.cantidad(),
                req.frecuencia(),
                req.fecha(),
                req.proximaFechaPago(),
                req.descripcion());
    }

    public static DonacionResponse toResponse(Donacion d) {
        return new DonacionResponse(
                d.getId() != null ? d.getId().getValue() : null,
                d.getUsuarioId() != null ? d.getUsuarioId().getValue() : null,
                d.getTipo() != null ? d.getTipo().name() : null,
                d.getCantidad(),
                d.getFrecuencia() != null ? d.getFrecuencia().name() : null,
                d.getFecha(),
                d.getProximaFechaPago(),
                d.getDescripcion());
    }

    public static DonacionEntity toEntity(Donacion d) {
        Integer usuarioId = null;
        if (d.getUsuarioId() != null) {
            usuarioId = d.getUsuarioId().getValue();
        }

        return DonacionEntity.builder()
                .id(d.getId() != null ? d.getId().getValue() : null)
                .usuarioId(usuarioId)
                .tipo(d.getTipo())
                .cantidad(d.getCantidad())
                .frecuencia(d.getFrecuencia())
                .fecha(d.getFecha())
                .proximaFechaPago(d.getProximaFechaPago())
                .descripcion(d.getDescripcion())
                .build();
    }

    public static Donacion toDomain(DonacionEntity e) {
        return Donacion.builder()
                .id(e.getId() != null ? new DonacionId(e.getId()) : null)
                .usuarioId(e.getUsuarioId() != null ? new UsuarioId(e.getUsuarioId()) : null)
                .tipo(e.getTipo())
                .cantidad(e.getCantidad())
                .frecuencia(e.getFrecuencia())
                .fecha(e.getFecha())
                .proximaFechaPago(e.getProximaFechaPago())
                .descripcion(e.getDescripcion())
                .build();
    }

    public static List<Donacion> toDomain(List<DonacionEntity> entities) {
        return entities.stream().map(DonacionMapper::toDomain).collect(Collectors.toList());
    }

    public static List<DonacionResponse> toResponse(List<Donacion> donaciones) {
        return donaciones.stream().map(DonacionMapper::toResponse).collect(Collectors.toList());
    }
}
