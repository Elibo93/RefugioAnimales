package es.refugio.animales.refugio.infraestructure.db.repository.mock.adopcion;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.animales.refugio.domain.model.adopcion.Adopcion;
import es.refugio.animales.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.animales.refugio.domain.model.animal.AnimalId;
import es.refugio.animales.refugio.domain.model.usuario.PersonaId;

public class AdopcionFactory {

        public static final Map<AdopcionId, Adopcion> getDemoData() {

                Map<AdopcionId, Adopcion> datos = new LinkedHashMap<>();

                datos.put(new AdopcionId(1),
                                new Adopcion(
                                                new AdopcionId(1),
                                                new PersonaId(1),
                                                new AnimalId(10),
                                                LocalDateTime.of(2025, 1, 10, 10, 0)

                                ));

                datos.put(new AdopcionId(2),
                                new Adopcion(
                                                new AdopcionId(2),
                                                new PersonaId(2),
                                                new AnimalId(10),
                                                LocalDateTime.of(2025, 1, 11, 11, 30)

                                ));

                datos.put(new AdopcionId(3),
                                new Adopcion(
                                                new AdopcionId(3),
                                                new PersonaId(3),
                                                new AnimalId(11),
                                                LocalDateTime.of(2025, 1, 12, 9, 45)

                                ));

                datos.put(new AdopcionId(4),
                                new Adopcion(
                                                new AdopcionId(4),
                                                new PersonaId(4),
                                                new AnimalId(12),
                                                LocalDateTime.of(2025, 1, 15, 17, 0)

                                ));

                return datos;
        }

        public static Adopcion create() {

                return new Adopcion(
                                new AdopcionId(99),
                                new PersonaId(99),
                                new AnimalId(99),
                                LocalDateTime.now());

        }
}
