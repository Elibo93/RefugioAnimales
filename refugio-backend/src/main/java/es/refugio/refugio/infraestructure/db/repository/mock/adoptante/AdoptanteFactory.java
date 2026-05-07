package es.refugio.refugio.infraestructure.db.repository.mock.adoptante;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;

public class AdoptanteFactory {

    public static final Map<AdoptanteId, Adoptante> getDemoData() {
        Map<AdoptanteId, Adoptante> datos = new LinkedHashMap<>();

        datos.put(new AdoptanteId(1),
                Adoptante.builder()
                        .id(new AdoptanteId(1))
                        .usuarioId(1)
                        .estadoValidacion(EstadoValidacion.APROBADO)
                        .fechaRegistro(LocalDateTime.of(2025, 1, 10, 10, 0))
                        .solicitudesIds(new ArrayList<>())
                        .adopcionesIds(new ArrayList<>())
                        .build());

        datos.put(new AdoptanteId(2),
                Adoptante.builder()
                        .id(new AdoptanteId(2))
                        .usuarioId(2)
                        .estadoValidacion(EstadoValidacion.PENDIENTE)
                        .fechaRegistro(LocalDateTime.of(2025, 2, 11, 11, 30))
                        .solicitudesIds(new ArrayList<>())
                        .adopcionesIds(new ArrayList<>())
                        .build());

        datos.put(new AdoptanteId(3),
                Adoptante.builder()
                        .id(new AdoptanteId(3))
                        .usuarioId(3)
                        .estadoValidacion(EstadoValidacion.RECHAZADO)
                        .fechaRegistro(LocalDateTime.of(2025, 2, 12, 0, 0))
                        .solicitudesIds(new ArrayList<>())
                        .adopcionesIds(new ArrayList<>())
                        .build());

        return datos;
    }

    public static Adoptante create() {
        return Adoptante.builder()
                .id(new AdoptanteId(99))
                .usuarioId(99)
                .estadoValidacion(EstadoValidacion.PENDIENTE)
                .fechaRegistro(LocalDateTime.now())
                .solicitudesIds(new ArrayList<>())
                .adopcionesIds(new ArrayList<>())
                .build();
    }
}