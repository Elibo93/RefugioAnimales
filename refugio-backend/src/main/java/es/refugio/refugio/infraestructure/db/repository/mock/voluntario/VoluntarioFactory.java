package es.refugio.refugio.infraestructure.db.repository.mock.voluntario;

import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;

public class VoluntarioFactory {

    public static Map<VoluntarioId, Voluntario> create() {
        Map<VoluntarioId, Voluntario> datos = new LinkedHashMap<>();

        datos.put(new VoluntarioId(1),
                Voluntario.builder()
                        .id(new VoluntarioId(1))
                        .usuarioId(new UsuarioId(1))
                        .disponibilidad("Lunes a Viernes")
                        .build());

        datos.put(new VoluntarioId(2),
                Voluntario.builder()
                        .id(new VoluntarioId(2))
                        .usuarioId(new UsuarioId(2))
                        .disponibilidad("Fines de semana")
                        .build());

        return datos;
    }

    public static Voluntario createSingle() {
        return Voluntario.builder()
                .id(new VoluntarioId(99))
                .usuarioId(new UsuarioId(99))
                .disponibilidad("Tardes")
                .build();
    }
}
