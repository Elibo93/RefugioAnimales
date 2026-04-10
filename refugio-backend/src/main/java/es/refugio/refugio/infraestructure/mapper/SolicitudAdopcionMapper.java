package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.SolicitudAdopcionEntity;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionResponse;

public class SolicitudAdopcionMapper {

    public static CreateSolicitudAdopcionCommand toCommand(SolicitudAdopcionRequest req) {
        return new CreateSolicitudAdopcionCommand(
                req.animalId(),
                req.adoptanteId(),
                req.fecha(),
                req.comentario()
        );
    }

    public static EditSolicitudAdopcionCommand toCommand(int id, SolicitudAdopcionRequest req) {
        return new EditSolicitudAdopcionCommand(
                new SolicitudAdopcionId(id),
                req.animalId(),
                req.adoptanteId(),
                req.fecha(),
                req.estado(),
                req.comentario()
        );
    }

    public static SolicitudAdopcionResponse toResponse(SolicitudAdopcion s) {
        return new SolicitudAdopcionResponse(
                s.getId() != null ? s.getId().getValue() : null,
                s.getAnimalId() != null ? s.getAnimalId().getValue() : null,
                s.getAdoptanteId() != null ? s.getAdoptanteId().getValue() : null,
                s.getFecha(),
                s.getEstado() != null ? s.getEstado().name() : null,
                s.getComentario()
        );
    }

    public static SolicitudAdopcionEntity toEntity(SolicitudAdopcion s) {
        AnimalEntity animalEntity = null;
        if (s.getAnimalId() != null) {
            animalEntity = AnimalEntity.builder().id(s.getAnimalId().getValue()).build();
        }

        AdoptanteEntity adoptanteEntity = null;
        if (s.getAdoptanteId() != null) {
            adoptanteEntity = AdoptanteEntity.builder().id(s.getAdoptanteId().getValue()).build();
        }

        return SolicitudAdopcionEntity.builder()
                .id(s.getId() != null ? s.getId().getValue() : null)
                .animal(animalEntity)
                .adoptante(adoptanteEntity)
                .fecha(s.getFecha())
                .estado(s.getEstado())
                .comentario(s.getComentario())
                .build();
    }

    public static SolicitudAdopcion toDomain(SolicitudAdopcionEntity e) {
        return SolicitudAdopcion.builder()
                .id(e.getId() != null ? new SolicitudAdopcionId(e.getId()) : null)
                .animalId(e.getAnimal() != null ? new AnimalId(e.getAnimal().getId()) : null)
                .adoptanteId(e.getAdoptante() != null ? new AdoptanteId(e.getAdoptante().getId()) : null)
                .fecha(e.getFecha())
                .estado(e.getEstado())
                .comentario(e.getComentario())
                .build();
    }

    public static List<SolicitudAdopcion> toDomain(List<SolicitudAdopcionEntity> lista) {
        return lista.stream()
                .map(SolicitudAdopcionMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static List<SolicitudAdopcionResponse> toResponse(List<SolicitudAdopcion> lista) {
        return lista.stream()
                .map(SolicitudAdopcionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
