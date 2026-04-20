package es.refugio.refugio.infraestructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import es.refugio.refugio.application.command.historial_medico.CreateHistorialMedicoCommand;
import es.refugio.refugio.application.command.historial_medico.EditHistorialMedicoCommand;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.HistorialMedicoEntity;
import es.refugio.refugio.infraestructure.web.dto.historial_medico.HistorialMedicoRequest;
import es.refugio.refugio.infraestructure.web.dto.historial_medico.HistorialMedicoResponse;

public class HistorialMedicoMapper {

    public static CreateHistorialMedicoCommand toCommand(HistorialMedicoRequest req) {
        return new CreateHistorialMedicoCommand(
                req.animalId(),
                req.fecha(),
                req.descripcion(),
                req.tratamiento(),
                req.veterinario()
        );
    }

    public static EditHistorialMedicoCommand toCommand(int id, HistorialMedicoRequest req) {
        return new EditHistorialMedicoCommand(
                new HistorialMedicoId(id),
                req.animalId(),
                req.fecha(),
                req.descripcion(),
                req.tratamiento(),
                req.veterinario()
        );
    }

    public static HistorialMedicoResponse toResponse(HistorialMedico h) {
        return new HistorialMedicoResponse(
                h.getId() != null ? h.getId().getValue() : null,
                h.getAnimalId() != null ? h.getAnimalId().getValue() : null,
                h.getFecha(),
                h.getDescripcion(),
                h.getTratamiento(),
                h.getVeterinario()
        );
    }

    public static HistorialMedicoEntity toEntity(HistorialMedico h) {
        AnimalEntity animalEntity = null;
        if (h.getAnimalId() != null) {
            animalEntity = AnimalEntity.builder().id(h.getAnimalId().getValue()).build();
        }

        return HistorialMedicoEntity.builder()
                .id(h.getId() != null ? h.getId().getValue() : null)
                .animal(animalEntity)
                .fecha(h.getFecha())
                .descripcion(h.getDescripcion())
                .tratamiento(h.getTratamiento())
                .veterinario(h.getVeterinario())
                .build();
    }

    public static HistorialMedico toDomain(HistorialMedicoEntity e) {
        return HistorialMedico.builder()
                .id(e.getId() != null ? new HistorialMedicoId(e.getId()) : null)
                .animalId(e.getAnimal() != null ? new AnimalId(e.getAnimal().getId()) : null)
                .fecha(e.getFecha())
                .descripcion(e.getDescripcion())
                .tratamiento(e.getTratamiento())
                .veterinario(e.getVeterinario())
                .build();
    }

    public static List<HistorialMedico> toDomain(List<HistorialMedicoEntity> lista) {
        return lista.stream()
                .map(HistorialMedicoMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static List<HistorialMedicoResponse> toResponse(List<HistorialMedico> lista) {
        return lista.stream()
                .map(HistorialMedicoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
