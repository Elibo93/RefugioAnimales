package es.refugio.refugio.infraestructure.db.repository.mock.usuario;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

public class UsuarioFactory {

        public static final Map<UsuarioId, Usuario> getDemoData() {

                Map<UsuarioId, Usuario> datos = new LinkedHashMap<>();

                datos.put(new UsuarioId(1),
                                new Usuario(
                                                new UsuarioId(1),
                                                "12345678A",
                                                "Diego",
                                                "Pérez",
                                                "diego@example.com",
                                                "600123123",
                                                "Calle Ejemplo 1",
                                                "2001-05-12",
                                                LocalDateTime.now()));

                datos.put(new UsuarioId(2),
                                new Usuario(
                                                new UsuarioId(2),
                                                "23456789B",
                                                "María",
                                                "Gómez",
                                                "maria@example.com",
                                                "611456789",
                                                "Avenida del Sol 22",
                                                "2003-09-30",
                                                LocalDateTime.now()));

                datos.put(new UsuarioId(3),
                                new Usuario(
                                                new UsuarioId(3),
                                                "34567890C",
                                                "Juan",
                                                "López",
                                                "juan@example.com",
                                                "622987654",
                                                "Plaza Mayor 10",
                                                "2004-01-18",
                                                LocalDateTime.now()));

                datos.put(new UsuarioId(4),
                                new Usuario(
                                                new UsuarioId(4),
                                                "45678901D",
                                                "Laura",
                                                "Santos",
                                                "laura@example.com",
                                                "633112233",
                                                "Calle Luna 45",
                                                "2002-08-20",
                                                LocalDateTime.now()));

                return datos;
        }

        public static Usuario create() {

                return new Usuario(
                                new UsuarioId(5),
                                "00000000Z",
                                "PersonaPrueba",
                                "ApellidoPrueba",
                                "prueba@example.com",
                                "600000000",
                                "Calle Prueba 123",
                                "2000-01-01",
                                LocalDateTime.now());
        }
}
