package es.refugio.frontend.service;

import es.refugio.frontend.client.AuthFeignClient;
import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Voluntarios en el Frontend.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class VoluntarioService {

    private final BackendFeignClient backendClient;
    private final AuthFeignClient authClient;

    public PaginatedResponse<VoluntarioRecord> fetchPaginatedVoluntarios(int page, int size, String q) {
        return fetchPaginatedVoluntarios(page, size, q, null, null);
    }

    public PaginatedResponse<VoluntarioRecord> fetchPaginatedVoluntarios(int page, int size, String q, Integer excludeTareaId, String excludeDate) {
        try {
            return backendClient.getVoluntariosPaginated(page - 1, size, q, excludeTareaId, excludeDate);
        } catch (Exception e) {
            return new PaginatedResponse<>(List.of(), 0, 0, page, size, false, false);
        }
    }

    public List<VoluntarioRecord> fetchAllVoluntarios() {
        try {
            PaginatedResponse<VoluntarioRecord> res = backendClient.getVoluntarios(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public VoluntarioRecord fetchVoluntarioById(Integer id) {
        try {
            return backendClient.getVoluntarioById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<UsuarioRecord> fetchAllUsuarios() {
        try {
            PaginatedResponse<UsuarioRecord> res = authClient.getUsuarios(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public UsuarioRecord fetchUsuarioById(Integer id) {
        try {
            return authClient.getUsuarioById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<PerfilLegalRecord> fetchAllPerfilesLegales() {
        try {
            return backendClient.getPerfilesLegales(1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public PerfilLegalRecord fetchPerfilLegalByUsuarioId(Integer usuarioId) {
        try {
            return backendClient.getPerfilLegalByUsuarioId(usuarioId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<TareaRecord> fetchTareasByVoluntario(Integer voluntarioId) {
        try {
            PaginatedResponse<TareaRecord> res = backendClient.getTareas(1000);
            if (res == null || res.items() == null) return List.of();
            return res.items().stream()
                    .filter(t -> t.voluntarioIds() != null && t.voluntarioIds().contains(voluntarioId))
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> fetchDisponibilidad(Integer voluntarioId) {
        try {
            return backendClient.getDisponibilidad(voluntarioId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public TareaRecord fetchTareaById(Integer id) {
        try {
            return backendClient.getTareaById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public VoluntarioRecord fetchVoluntarioByUsuarioId(Integer usuarioId) {
        try {
            return backendClient.getVoluntarioByUsuarioId(usuarioId);
        } catch (Exception e) {
            return null;
        }
    }

    public UsuarioRecord fetchMe() {
        try {
            return authClient.getMe();
        } catch (Exception e) {
            return null;
        }
    }

    public UsuarioRecord crearUsuario(String email, String contrasena, String rol) {
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email", email);
        userBody.put("contrasena", contrasena);
        userBody.put("rol", rol);
        try {
            // Utilizamos el endpoint create, pero como nos devuelve un Map necesitamos volver a recuperar el usuario.
            // (Para simplicar, solo pasamos los params si podemos mapear a record. Si el feign devuelve Map, hacemos manual o cambiamos feign)
            Map<String, Object> res = authClient.createUserAuth(userBody);
            if (res != null && res.get("id") != null) {
                return authClient.getUsuarioById(Integer.valueOf(res.get("id").toString()));
            }
        } catch (Exception ignored) {}
        return null;
    }

    public List<VoluntarioRecord> fetchVoluntariosPendientes() {
        try {
            return backendClient.getVoluntariosPendientes(1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public void crearVoluntarioYPerfil(Integer usuarioId, String nombre, String apellido, String dni, String direccion, String telefono, String fechaNacimiento, String especialidad, String disponibilidad) {
        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", usuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("direccion", direccion);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("fechaNacimiento", fechaNacimiento);
        backendClient.createPerfilLegal(bodyPerfil);

        Map<String, Object> bodyVoluntario = new HashMap<>();
        bodyVoluntario.put("usuarioId", usuarioId);
        bodyVoluntario.put("disponibilidad", disponibilidad);
        bodyVoluntario.put("especialidad", especialidad);
        backendClient.createVoluntario(bodyVoluntario);
    }

    public void editarVoluntarioYPerfil(Integer id, Integer usuarioId, String nombre, String apellido, String email, String dni, String direccion, String telefono, String fechaNacimiento, String especialidad, String disponibilidad) {
        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        if (disponibilidad != null) body.put("disponibilidad", disponibilidad);
        if (especialidad != null) body.put("especialidad", especialidad);
        backendClient.updateVoluntario(id, body);

        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", usuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("direccion", direccion);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("fechaNacimiento", fechaNacimiento);
        backendClient.createPerfilLegal(bodyPerfil);

        if (email != null && !email.trim().isEmpty()) {
            try {
                UsuarioRecord user = fetchUsuarioById(usuarioId);
                if (user != null) {
                    Map<String, Object> bodyUser = new HashMap<>();
                    bodyUser.put("id", user.id());
                    bodyUser.put("username", user.username());
                    bodyUser.put("email", email);
                    bodyUser.put("rol", user.rol());
                    bodyUser.put("contrasena", "secret_placeholder");
                    authClient.updateUserAuth(usuarioId, bodyUser);
                }
            } catch (Exception ignored) {}
        }
    }

    public void eliminarVoluntario(Integer id) {
        backendClient.deleteVoluntario(id);
    }

    public void aprobarVoluntario(Integer id) {
        backendClient.aprobarVoluntario(id);
    }

    public void rechazarVoluntario(Integer id) {
        backendClient.rechazarVoluntario(id);
    }

    public void aprobarSolicitudVoluntario(Integer id) {
        backendClient.aprobarVoluntario(id);
    }

    public void rechazarSolicitudVoluntario(Integer id) {
        backendClient.rechazarVoluntario(id);
    }
    
    public void addDisponibilidad(Integer voluntarioId, String diaSemana, String horaInicio, String horaFin) {
        Map<String, Object> body = new HashMap<>();
        body.put("diaSemana", diaSemana);
        body.put("horaInicio", horaInicio);
        body.put("horaFin", horaFin);
        backendClient.addDisponibilidad(voluntarioId, body);
    }
    
    public void deleteDisponibilidad(Integer voluntarioId, Integer disponibilidadId) {
        backendClient.deleteDisponibilidad(voluntarioId, disponibilidadId);
    }

    public Map<String, Object> buildListarModelData(int page, int size, String q, boolean modoSeleccion, Integer tareaIdSeleccion) {
        Map<String, Object> modelData = new HashMap<>();
        
        Integer excludeTareaId = null;
        String excludeDate = null;
        List<Integer> assignedIds = new ArrayList<>();
        boolean isWeekend = false;
        String dayOfWeekSpanish = "";
        LocalDateTime limit = null;

        if (modoSeleccion && tareaIdSeleccion != null) {
            try {
                TareaRecord tarea = fetchTareaById(tareaIdSeleccion);
                if (tarea != null) {
                    modelData.put("tareaNombreSeleccion", tarea.descripcion());
                    excludeTareaId = tareaIdSeleccion;

                    List<Integer> vIds = tarea.voluntarioIds();
                    if (vIds != null) {
                        assignedIds.addAll(vIds);
                    }
                    modelData.put("assignedVoluntarioIds", assignedIds);

                    limit = tarea.fechaLimite();
                    if (limit != null) {
                        excludeDate = limit.toLocalDate().toString();
                        DayOfWeek dow = limit.getDayOfWeek();
                        isWeekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);
                        switch (dow) {
                            case MONDAY: dayOfWeekSpanish = "LUNES"; break;
                            case TUESDAY: dayOfWeekSpanish = "MARTES"; break;
                            case WEDNESDAY: dayOfWeekSpanish = "MIERCOLES"; break;
                            case THURSDAY: dayOfWeekSpanish = "JUEVES"; break;
                            case FRIDAY: dayOfWeekSpanish = "VIERNES"; break;
                            case SATURDAY: dayOfWeekSpanish = "SABADO"; break;
                            case SUNDAY: dayOfWeekSpanish = "DOMINGO"; break;
                        }
                    }
                }
            } catch (Exception e) {}
        }

        PaginatedResponse<VoluntarioRecord> pagination = fetchPaginatedVoluntarios(page, size, q, excludeTareaId, excludeDate);
        List<VoluntarioRecord> voluntarios = new ArrayList<>(pagination.items());

        if (modoSeleccion && tareaIdSeleccion != null) {
            final boolean finalIsWeekend = isWeekend;
            final String finalDayOfWeekSpanish = dayOfWeekSpanish;
            final LocalDateTime finalLimit = limit;

            voluntarios.removeIf(v -> {
                if (assignedIds.contains(v.id())) return true;
                if (finalLimit != null && v.disponibilidad() != null) {
                    String disp = v.disponibilidad().toUpperCase();
                    if (disp.contains("FINES DE SEMANA") && !finalIsWeekend) return true;
                    if ((disp.equals("LUNES") || disp.equals("MARTES") || disp.equals("MIERCOLES") || disp.equals("MIÉRCOLES") || 
                         disp.equals("JUEVES") || disp.equals("VIERNES") || disp.equals("SABADO") || disp.equals("SÁBADO") || disp.equals("DOMINGO"))
                         && !disp.replace("Á", "A").replace("É", "E").equals(finalDayOfWeekSpanish)) {
                        return true;
                    }
                }
                return false;
            });
        }
        
        List<UsuarioRecord> usuarios = fetchAllUsuarios();
        List<PerfilLegalRecord> perfilesLegales = fetchAllPerfilesLegales();

        Map<String, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(String.valueOf(u.id()), u);
        }

        Map<String, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfilesLegales) {
            if (p.usuarioId() != null) perfilesMap.put(String.valueOf(p.usuarioId()), p);
        }

        modelData.put("voluntarios", voluntarios);
        modelData.put("pagination", pagination);
        modelData.put("usuariosMap", usuariosMap);
        modelData.put("perfilesMap", perfilesMap);
        modelData.put("query", q);
        modelData.put("modoSeleccion", modoSeleccion);
        modelData.put("tareaIdSeleccion", tareaIdSeleccion);
        return modelData;
    }

    public List<VoluntarioEncontradoRecord> buildSugerenciasModelData(String q, String fechaLimite, List<Integer> voluntarioIds) {
        if (q == null || q.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<VoluntarioRecord> voluntarios = fetchAllVoluntarios();
        List<PerfilLegalRecord> perfiles = fetchAllPerfilesLegales();
        List<UsuarioRecord> usuarios = fetchAllUsuarios();
        
        String diaSemanaRequerido = null;
        if (fechaLimite != null && !fechaLimite.trim().isEmpty()) {
            try {
                LocalDateTime fecha = LocalDateTime.parse(fechaLimite);
                DayOfWeek day = fecha.getDayOfWeek();
                switch (day) {
                    case MONDAY: case TUESDAY: case WEDNESDAY: case THURSDAY: case FRIDAY:
                        diaSemanaRequerido = "ENTRE_SEMANA"; break;
                    case SATURDAY: case SUNDAY:
                        diaSemanaRequerido = "FINES_DE_SEMANA"; break;
                }
            } catch (Exception e) {}
        }

        Map<Integer, PerfilLegalRecord> perfilesMap = new HashMap<>();
        for (PerfilLegalRecord p : perfiles) {
            if (p.usuarioId() != null) perfilesMap.put(p.usuarioId(), p);
        }

        Map<Integer, UsuarioRecord> usuariosMap = new HashMap<>();
        for (UsuarioRecord u : usuarios) {
            usuariosMap.put(u.id(), u);
        }

        String query = q.toLowerCase();
        List<VoluntarioEncontradoRecord> voluntariosEncontrados = new ArrayList<>();
        
        for (VoluntarioRecord v : voluntarios) {
            if (!"APROBADO".equals(v.estado())) continue;
            if (voluntarioIds != null && voluntarioIds.contains(v.id())) continue;

            if (diaSemanaRequerido != null && v.disponibilidad() != null) {
                String disp = v.disponibilidad().toUpperCase();
                if (!disp.contains(diaSemanaRequerido) && !disp.contains("FLEXIBLE") && !disp.contains("CUALQUIERA")) {
                    if (diaSemanaRequerido.equals("FINES_DE_SEMANA") && (disp.contains("MAÑANAS") || disp.contains("TARDES"))) {
                        if (!disp.equals("MAÑANAS") && !disp.equals("TARDES") && !disp.equals("FLEXIBLE")) {
                            continue;
                        }
                    } else if (diaSemanaRequerido.equals("ENTRE_SEMANA") && disp.equals("FINES_DE_SEMANA")) {
                        continue; 
                    }
                }
            }

            if (v.usuarioId() != null) {
                int uId = v.usuarioId();
                PerfilLegalRecord perfil = perfilesMap.get(uId);
                UsuarioRecord user = usuariosMap.get(uId);

                String nombre = perfil != null && perfil.nombre() != null ? perfil.nombre() : "";
                String apellido = perfil != null && perfil.apellido() != null ? perfil.apellido() : "";
                String email = user != null && user.email() != null ? user.email() : "";
                String username = user != null && user.username() != null ? user.username() : "";

                if (nombre.toLowerCase().contains(query) || apellido.toLowerCase().contains(query) ||
                        email.toLowerCase().contains(query) || username.toLowerCase().contains(query)) {
                    voluntariosEncontrados.add(new VoluntarioEncontradoRecord(
                        v.id(), nombre, apellido, email, username
                    ));
                }
            }
        }
        return voluntariosEncontrados;
    }
}
