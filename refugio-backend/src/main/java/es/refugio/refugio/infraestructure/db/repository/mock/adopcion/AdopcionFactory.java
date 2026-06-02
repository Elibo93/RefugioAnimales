package es.refugio.refugio.infraestructure.db.repository.mock.adopcion;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;

public class AdopcionFactory {

        public static final Map<AdopcionId, Adopcion> getDemoData() {

                Map<AdopcionId, Adopcion> datos = new LinkedHashMap<>();

                datos.put(new AdopcionId(1),
                                Adopcion.builder()
                                                .id(new AdopcionId(1))
                                                .adoptanteId(new AdoptanteId(1))
                                                .animalId(new AnimalId(10))
                                                .fechaAdopcion(LocalDateTime.of(2025, 1, 10, 10, 0))
                                                .estado(EstadoAdopcion.COMPLETADA)
                                                .contrato("contrato_luna.pdf")
                                                .build());

                datos.put(new AdopcionId(2),
                                Adopcion.builder()
                                                .id(new AdopcionId(2))
                                                .adoptanteId(new AdoptanteId(2))
                                                .animalId(new AnimalId(10))
                                                .fechaAdopcion(LocalDateTime.of(2025, 1, 11, 11, 30))
                                                .estado(EstadoAdopcion.EN_PERIODO_ADAPTACION)
                                                .contrato("contrato_max.pdf")
                                                .build());

                datos.put(new AdopcionId(3),
                                Adopcion.builder()
                                                .id(new AdopcionId(3))
                                                .adoptanteId(new AdoptanteId(3))
                                                .animalId(new AnimalId(11))
                                                .fechaAdopcion(LocalDateTime.of(2025, 1, 12, 9, 45))
                                                .estado(EstadoAdopcion.COMPLETADA)
                                                .contrato("contrato_misu.pdf")
                                                .build());

                datos.put(new AdopcionId(4),
                                Adopcion.builder()
                                                .id(new AdopcionId(4))
                                                .adoptanteId(new AdoptanteId(4))
                                                .animalId(new AnimalId(12))
                                                .fechaAdopcion(LocalDateTime.of(2025, 1, 15, 17, 0))
                                                .estado(EstadoAdopcion.CANCELADA)
                                                .contrato(null)
                                                .build());

                return datos;
        }

        public static Adopcion create() {
                return Adopcion.builder()
                                .id(new AdopcionId(99))
                                .adoptanteId(new AdoptanteId(99))
                                .animalId(new AnimalId(99))
                                .fechaAdopcion(LocalDateTime.now())
                                .estado(EstadoAdopcion.EN_PERIODO_ADAPTACION)
                                .contrato("pendiente.pdf")
                                .build();
        }
}