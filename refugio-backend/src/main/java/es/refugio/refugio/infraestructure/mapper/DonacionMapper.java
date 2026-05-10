package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.application.command.donacion.EditDonacionCommand;
import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.infraestructure.db.jpa.entity.DonacionEntity;
import es.refugio.refugio.infraestructure.web.dto.donacion.DonacionRequest;
import es.refugio.refugio.infraestructure.web.dto.donacion.DonacionResponse;

public class DonacionMapper {

    public static CreateDonacionCommand toCommand(DonacionRequest req) {
        return CreateDonacionCommand.builder()
                .usuarioId(req.usuarioId())
                .objetivoId(req.objetivoId())
                .tipo(req.tipo())
                .cantidad(req.cantidad())
                .frecuencia(req.frecuencia())
                .fecha(req.fecha())
                .proximaFechaPago(req.proximaFechaPago())
                .descripcion(req.descripcion())
                .build();
    }

    public static EditDonacionCommand toCommand(int id, DonacionRequest req) {
        return EditDonacionCommand.builder()
                .id(new DonacionId(id))
                .usuarioId(req.usuarioId())
                .objetivoId(req.objetivoId())
                .tipo(req.tipo())
                .cantidad(req.cantidad())
                .frecuencia(req.frecuencia())
                .fecha(req.fecha())
                .proximaFechaPago(req.proximaFechaPago())
                .descripcion(req.descripcion())
                .build();
    }

    public static DonacionResponse toResponse(Donacion d) {
        return new DonacionResponse(
                d.getId() != null ? d.getId().getValue() : null,
              

     d.getUsuarioId() != null ? d.getUsuarioId().getValue() : null,
                d.getObjetivoId() != null ? d.getObjetivoId().getValue() : null,
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
        Integer objetivoId = null;
        if (d.getObjetivoId() != null) {
            objetivoId = d.getObjetivoId().getValue();
        }

        return DonacionEntity.builder()
                .id(d.getId() != null ? d.getId().getValue() : null)
                .usuarioId(usuarioId)
                .objetivoId(objetivoId)
                .tipo(d.getTipo() != null ? d.getTipo().name() : null)
                .cantidad(d.getCantidad())
                .frecuencia(d.getFrecuencia() != null ? d.getFrecuencia().name() : null)
                .fecha(d.getFecha())
                .proximaFechaPago(d.getProximaFechaPago())
                .descripcion(d.getDescripcion())
                .build();
    }

    public static Donacion toDomain(DonacionEntity e) {
        return Donacion.builder()
                .id(e.getId() != null ? new DonacionId(e.getId()) : null)
                .usuarioId(e.getUsuarioId() != null ? new UsuarioId(e.getUsuarioId()) : null)
                .objetivoId(e.getObjetivoId() != null ? new ObjetivoDonacionId(e.getObjetivoId()) : null)
                .tipo(mapTipo(e.getTipo()))
                .cantidad(e.getCantidad())
                .frecuencia(mapFrecuencia(e.getFrecuencia()))
                .fecha(e.getFecha())
                .proximaFechaPago(e.getProximaFechaPago())
                .descripcion(e.getDescripcion())
                .build();
    }

    private static TipoDonacion mapTipo(String tipo) {
        if (tipo == null) return null;
        try {
            return TipoDonacion.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            return TipoDonacion.OTRO;
        }
    }

    private static FrecuenciaDonacion mapFrecuencia(String frecuencia) {
        if (frecuencia == null) return null;
        try {
            return FrecuenciaDonacion.valueOf(frecuencia);
        } catch (IllegalArgumentException e) {
            return FrecuenciaDonacion.UNICA;
        }
    }

    public static List<Donacion> toDomain(List<DonacionEntity> entities) {
        return entities.stream().map(DonacionMapper::toDomain).collect(Collectors.toList());
    }

    public static List<DonacionResponse> toResponse(List<Donacion> donaciones) {
        return donaciones.stream().map(DonacionMapper::toResponse).collect(Collectors.toList());
    }
}
