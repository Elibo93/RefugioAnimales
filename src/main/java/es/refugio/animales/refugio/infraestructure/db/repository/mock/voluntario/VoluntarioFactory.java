package es.refugio.animales.refugio.infraestructure.db.repository.mock.voluntario;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
import es.refugio.animales.refugio.domain.model.voluntario.VoluntarioId;

public class VoluntarioFactory {

    public static final Map<VoluntarioId, Voluntario> getDemoData(){
        Map<VoluntarioId, Voluntario> datos = new LinkedHashMap<>();

        datos.put(
            new VoluntarioId(1),
            new Voluntario(
                new VoluntarioId(1),
                "Laura",
                "García",
                "Dibujo",
                "laura.garcia@refugio.com", 
                "612345678",
                LocalDateTime.now()
            )
        );

        datos.put(
            new VoluntarioId(2),
            new Voluntario(
                new VoluntarioId(2),
                "Miguel",
                "Fernández",
                "Música",
                "miguel.fernandez@refugio.com",
                "622334455",
                LocalDateTime.now()
            )
        );

        datos.put(
            new VoluntarioId(3),
            new Voluntario(
                new VoluntarioId(3),
                "Sara",
                "López",
                "Teatro",
                "sara.lopez@refugio.com",
                "633221144",
                LocalDateTime.now()
            )
        );

        datos.put(
            new VoluntarioId(4),
            new Voluntario(
                new VoluntarioId(4),
                "Álvaro",
                "Martínez",
                "Pintura",
                "alvaro.martinez@refugio.com",
                "644556677",
                LocalDateTime.now()
            )
        );

        return datos;
    }

    public static Voluntario create() {
        return new Voluntario(
                new VoluntarioId(5),
                "Andrés",
                "Carmelo",
                "Diseño",
                "andres.carmelo@refugio.com",
                "654256672",
                LocalDateTime.now());
    }
}
















