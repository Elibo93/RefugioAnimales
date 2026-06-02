package es.refugio.frontend.service;

import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDate;

/**
 * Servicio para gestionar las operaciones relacionadas con Tareas en el Frontend.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class TareaService {

    private final BackendFeignClient backendClient;
    private final VoluntarioService voluntarioService;

    public List<TareaRecord> fetchAllTareas() {
        try {
            PaginatedResponse<TareaRecord> res = backendClient.getTareas(9999);
            return res != null && res.items() != null ? res.items() : List.of();
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

    public void crearTarea(Map<String, Object> body) {
        backendClient.createTarea(body);
    }

    public void editarTarea(Integer id, Map<String, Object> body) {
        backendClient.updateTarea(id, body);
    }

    public void eliminarTarea(Integer id) {
        backendClient.deleteTarea(id);
    }

    public void actualizarEstadoTarea(Integer id, String estado) {
        Map<String, Object> body = Map.of("estado", estado);
        backendClient.updateTarea(id, body);
    }

    public ResponseEntity<byte[]> descargarPdfTarea(Integer id) {
        return backendClient.descargarPdfTarea(id);
    }

    public String fetchHtmlTarea(Integer id) {
        try {
            return backendClient.getHtmlTarea(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> fetchHistorial(Integer id) {
        try {
            return backendClient.getHistorialTarea(id);
        } catch (Exception e) {
            return List.of();
        }
    }

    public Map<String, Object> buildListarModelData(String prioridad, String estado, Integer myVoluntarioId,
            Integer voluntarioIdFiltro, boolean modoSeleccion, Integer voluntarioIdSeleccion, int page, int size) {

        Map<String, Object> modelData = new HashMap<>();

        if (modoSeleccion && voluntarioIdSeleccion != null) {
            try {
                VoluntarioRecord v = voluntarioService.fetchVoluntarioById(voluntarioIdSeleccion);
                if (v != null && v.usuarioId() != null) {
                    PerfilLegalRecord p = voluntarioService.fetchPerfilLegalByUsuarioId(v.usuarioId());
                    if (p != null) {
                        modelData.put("voluntarioNombreSeleccion", p.nombre() + " " + p.apellido());
                    }
                }
            } catch (Exception ignored) {
            }
        }

        List<DisponibilidadRecord> disponibilidades = Collections.emptyList();
        if (modoSeleccion && voluntarioIdSeleccion != null) {
            try {
                List<Map<String, Object>> listDisponibilidad = voluntarioService
                        .fetchDisponibilidad(voluntarioIdSeleccion);
                if (listDisponibilidad != null) {
                    List<DisponibilidadRecord> mapped = new ArrayList<>();
                    for (Map<String, Object> map : listDisponibilidad) {
                        try {
                            String f = map.get("fecha") != null ? map.get("fecha").toString() : null;
                            LocalDate date = f != null ? LocalDate.parse(f) : null;
                            String st = map.get("estado") != null ? map.get("estado").toString() : null;
                            mapped.add(new DisponibilidadRecord(null, date, null, st));
                        } catch (Exception ignored) {
                        }
                    }
                    disponibilidades = mapped;
                }
            } catch (Exception ignored) {
            }
        }

        List<TareaRecord> todasTareasFiltradas = fetchFiltered(prioridad, estado, myVoluntarioId, voluntarioIdFiltro,
                voluntarioIdSeleccion, disponibilidades);
        PaginatedResponse<TareaRecord> pagination = paginateList(todasTareasFiltradas, page, size);
        List<TareaRecord> tareas = pagination.items();
        modelData.put("todasTareasFiltradas", todasTareasFiltradas);

        List<VoluntarioRecord> voluntarios = voluntarioService.fetchAllVoluntarios();

        Set<Integer> assignedTaskIds = new HashSet<>();
        if (modoSeleccion && voluntarioIdSeleccion != null) {
            for (TareaRecord t : tareas) {
                if (t.voluntarioIds() != null && t.voluntarioIds().contains(voluntarioIdSeleccion)) {
                    if (t.id() != null) {
                        assignedTaskIds.add(t.id());
                    }
                }
            }
        }
        modelData.put("alreadyAssignedTaskIds", assignedTaskIds);

        boolean hasFilters = (prioridad != null && !"ALL".equals(prioridad))
                || (estado != null && !"ALL".equals(estado));
        modelData.put("selectedPrioridad", prioridad != null ? prioridad : "ALL");
        modelData.put("selectedEstado", estado != null ? estado : "ALL");
        modelData.put("hasFilters", hasFilters);

        Map<String, String> voluntarioUsuarioIds = new HashMap<>();
        for (VoluntarioRecord v : voluntarios) {
            if (v.id() != null && v.usuarioId() != null) {
                voluntarioUsuarioIds.put(v.id().toString(), v.usuarioId().toString());
            }
        }

        Map<String, String> voluntarioNombres = fetchVoluntarioNombres();
        modelData.put("tareaList", tareas);
        modelData.put("pagination", pagination);
        modelData.put("voluntarioNombres", voluntarioNombres);
        modelData.put("voluntarioUsuarioIds", voluntarioUsuarioIds);

        return modelData;
    }

    public Map<String, String> fetchVoluntarioNombres() {
        Map<String, String> nombres = new HashMap<>();
        try {
            List<VoluntarioRecord> voluntarios = voluntarioService.fetchAllVoluntarios();
            List<PerfilLegalRecord> perfiles = voluntarioService.fetchAllPerfilesLegales();
            List<UsuarioRecord> usuarios = voluntarioService.fetchAllUsuarios();

            Map<Integer, String> perfilMap = new HashMap<>();
            for (PerfilLegalRecord p : perfiles) {
                if (p.usuarioId() != null) {
                    perfilMap.put(p.usuarioId(), p.nombre() + " " + p.apellido());
                }
            }

            Map<Integer, String> userMap = new HashMap<>();
            for (UsuarioRecord u : usuarios) {
                userMap.put(u.id(), u.username());
            }

            for (VoluntarioRecord v : voluntarios) {
                if (v.id() != null && v.usuarioId() != null) {
                    String nombre = perfilMap.get(v.usuarioId());
                    if (nombre == null)
                        nombre = userMap.get(v.usuarioId());
                    if (nombre == null)
                        nombre = "Voluntario " + v.id();
                    nombres.put(v.id().toString(), nombre.trim());
                }
            }
        } catch (Exception ignored) {
        }
        return nombres;
    }

    public List<TareaRecord> fetchFiltered(String prioridad, String estado, Integer myVoluntarioId,
            Integer voluntarioIdFiltro,
            Integer voluntarioIdSeleccion, List<DisponibilidadRecord> disponibilidades) {
        try {
            List<TareaRecord> allTareas = fetchAllTareas();

            return allTareas.stream()
                    .filter(t -> {
                        if (myVoluntarioId != null) {
                            if (t.voluntarioIds() == null || !t.voluntarioIds().contains(myVoluntarioId)) {
                                return false;
                            }
                        }
                        if (voluntarioIdFiltro != null) {
                            if (t.voluntarioIds() == null || !t.voluntarioIds().contains(voluntarioIdFiltro)) {
                                return false;
                            }
                        }
                        if (voluntarioIdSeleccion != null) {
                            if (t.voluntarioIds() != null && t.voluntarioIds().contains(voluntarioIdSeleccion)) {
                                return false;
                            }
                            if (t.fechaLimite() != null) {
                                LocalDate taskDate = t.fechaLimite().toLocalDate();
                                boolean isUnavailable = disponibilidades.stream()
                                        .anyMatch(d -> d.fecha() != null && d.fecha().equals(taskDate)
                                                && "NO_DISPONIBLE".equals(d.estado()));
                                if (isUnavailable) {
                                    return false;
                                }
                            }
                        }
                        if (prioridad != null && !"ALL".equalsIgnoreCase(prioridad)) {
                            if (t.prioridad() == null || !prioridad.equalsIgnoreCase(t.prioridad())) {
                                return false;
                            }
                        }
                        if (estado != null && !"ALL".equalsIgnoreCase(estado)) {
                            if (t.estado() == null || !estado.equalsIgnoreCase(t.estado())) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private PaginatedResponse<TareaRecord> paginateList(List<TareaRecord> filtered, int page, int size) {
        int totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        if (totalPages == 0)
            totalPages = 1;

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<TareaRecord> paginatedItems = Collections.emptyList();
        if (fromIndex < totalElements && fromIndex >= 0) {
            paginatedItems = filtered.subList(fromIndex, toIndex);
        }

        boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1;

        return new PaginatedResponse<>(paginatedItems, totalPages, totalElements, page, size, hasNext, hasPrevious);
    }
}
