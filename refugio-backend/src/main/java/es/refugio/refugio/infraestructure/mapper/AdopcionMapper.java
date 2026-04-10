package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdopcionEntity;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.adopcion.AdopcionResponse;

public class AdopcionMapper {

    public static CreateAdopcionCommand toCommand(AdopcionRequest req) {
        return new CreateAdopcionCommand(
                req.adoptanteId(),
                req.animalId(),
                EstadoAdopcion.valueOf(req.estado()),
                req.contrato());
    }

    public static EditAdopcionCommand toCommand(int id, AdopcionRequest req) {
        return new EditAdopcionCommand(
                new AdopcionId(id),
                req.animalId(),
                req.adoptanteId(),
                req.fechaAdopcion(),
                req.estado(),
                req.contrato());
    }

    public static AdopcionResponse toResponse(Adopcion a) {
        return new AdopcionResponse(
                a.getId() != null ? a.getId().getValue() : null,
                a.getAnimalId() != null ? a.getAnimalId().getValue() : null,
                a.getAdoptanteId() != null ? a.getAdoptanteId().getValue() : null,
                // Si tienes un Dto nuevo que soporte la solicitud, añadelo, si no el Request se queda igual
                a.getFechaAdopcion(),
                a.getEstado() != null ? a.getEstado().name() : null,
                a.getContrato());
    }

    public static AdopcionEntity toEntity(Adopcion a) {
        AnimalEntity animalEntity = null;
        if (a.getAnimalId() != null) {
            animalEntity = AnimalEntity.builder().id(a.getAnimalId().getValue()).build();
        }

        AdoptanteEntity adoptanteEntity = null;
        if (a.getAdoptanteId() != null) {
            adoptanteEntity = AdoptanteEntity.builder().id(a.getAdoptanteId().getValue()).build();
        }

        es.refugio.refugio.infraestructure.db.jpa.entity.SolicitudAdopcionEntity solicitudEntity = null;
        if (a.getSolicitudAdopcionId() != null) {
            solicitudEntity = es.refugio.refugio.infraestructure.db.jpa.entity.SolicitudAdopcionEntity.builder()
                .id(a.getSolicitudAdopcionId())
                .build();
        }

        return AdopcionEntity.builder()
                .id(a.getId() != null ? a.getId().getValue() : null)
                .animal(animalEntity)
                .adoptante(adoptanteEntity)
                .solicitudAdopcion(solicitudEntity)
                .fechaAdopcion(a.getFechaAdopcion())
                .estado(a.getEstado())
                .contrato(a.getContrato())
                .build();
    }

    public static Adopcion toDomain(AdopcionEntity e) {
        return Adopcion.builder()
                .id(e.getId() != null ? new AdopcionId(e.getId()) : null)
                .animalId(e.getAnimal() != null ? new AnimalId(e.getAnimal().getId()) : null)
                .adoptanteId(e.getAdoptante() != null ? new AdoptanteId(e.getAdoptante().getId()) : null)
                .solicitudAdopcionId(e.getSolicitudAdopcion() != null ? e.getSolicitudAdopcion().getId() : null)
                .fechaAdopcion(e.getFechaAdopcion())
                .estado(e.getEstado())
                .contrato(e.getContrato())
                .build();
    }

    public static List<Adopcion> toDomain(List<AdopcionEntity> lista) {
        return lista.stream()
                .map(AdopcionMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static List<AdopcionResponse> toResponse(List<Adopcion> lista) {
        return lista.stream()
                .map(AdopcionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
