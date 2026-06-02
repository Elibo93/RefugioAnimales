package es.refugio.frontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import es.refugio.frontend.client.AuthFeignClient;
import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.UsuarioRecord;
import es.refugio.frontend.web.dto.PerfilLegalRecord;
import es.refugio.frontend.web.dto.PersonaCompletaRecord;
import es.refugio.frontend.web.dto.PaginatedResponse;
import es.refugio.frontend.web.dto.AdoptanteRecord;
import es.refugio.frontend.web.dto.VoluntarioRecord;
import es.refugio.frontend.web.dto.UsuarioEncontradoRecord;
import es.refugio.frontend.web.dto.UsuarioMetricasRecord;
import es.refugio.frontend.web.dto.SolicitudAdopcionRecord;
import es.refugio.frontend.web.dto.AdopcionRecord;
import es.refugio.frontend.web.dto.TareaRecord;
import es.refugio.frontend.web.dto.AnimalRecord;

/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Usuario.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final AuthFeignClient authClient;
    private final BackendFeignClient backendClient;

    public Map<String, Object> createUserAuth(Map<String, Object> userBody) {
        return authClient.createUserAuth(userBody);
    }

    public void createPerfilLegal(Map<String, Object> legalBody) {
        backendClient.createPerfilLegal(legalBody);
    }

    public void updateUserAuth(Integer id, Map<String, Object> userBody) {
        authClient.updateUserAuth(id, userBody);
    }

    public ResponseEntity<?> verificarPassword(Integer id, String password) {
        return authClient.verificarPassword(id, password != null ? password.trim() : "");
    }

    public void cambiarPassword(Integer id, Map<String, String> body) {
        authClient.cambiarPassword(id, body);
    }

    public void deletePerfilLegal(Integer id) {
        backendClient.deletePerfilLegal(id);
    }

    public void deleteUsuarioAuth(Integer id) {
        authClient.deleteUsuarioAuth(id);
    }

    public List<UsuarioRecord> fetchAllUsuarios() {
        try {
            PaginatedResponse<UsuarioRecord> res = authClient.getUsuarios(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public UsuarioMetricasRecord fetchMetricasGamificacion(Integer id) {
        try {
            return backendClient.fetchMetricasGamificacion(id);
        } catch (Exception e) {
            return null;
        }
    }

    public UsuarioRecord fetchUsuarioById(Integer id) {
        try {
            return authClient.getUsuarioById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public PerfilLegalRecord fetchPerfilLegalByUsuarioId(Integer id) {
        try {
            return backendClient.getPerfilLegalByUsuarioId(id);
        } catch (Exception e) {
            return null;
        }
    }

    public AdoptanteRecord fetchAdoptanteByUsuarioId(Integer id) {
        try {
            return backendClient.getAdoptanteByUsuarioId(id);
        } catch (Exception e) {
            return null;
        }
    }

    public VoluntarioRecord fetchVoluntarioByUsuarioId(Integer id) {
        try {
            return backendClient.getVoluntarioByUsuarioId(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<SolicitudAdopcionRecord> fetchSolicitudesAdopcionByAdoptanteId(Integer adoptanteId) {
        try {
            return backendClient.getSolicitudesAdopcionByAdoptanteId(adoptanteId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<AdopcionRecord> fetchAdopcionesByAdoptanteId(Integer adoptanteId) {
        try {
            return backendClient.getAdopcionesByAdoptanteId(adoptanteId, 1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<TareaRecord> fetchAllTareas() {
        try {
            PaginatedResponse<TareaRecord> res = backendClient.getTareas(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> fetchDisponibilidades(Integer voluntarioId) {
        try {
            return backendClient.getDisponibilidad(voluntarioId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> fetchLogrosUsuario(Integer id) {
        try {
            return backendClient.getLogrosUsuario(id);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> fetchTodosLosLogros() {
        try {
            return backendClient.getTodosLosLogros();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<PerfilLegalRecord> fetchAllPerfilesLegales() {
        try {
            return backendClient.getPerfilesLegales(1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public AnimalRecord fetchAnimalById(Integer id) {
        try {
            return backendClient.getAnimalById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> createAdoptante(Map<String, Object> adoptanteReq) {
        return backendClient.createAdoptante(adoptanteReq);
    }

    public Map<String, Object> buildListarModelData(String q, String rol, int page, int size) {
        Map<String, Object> modelData = new HashMap<>();
        List<UsuarioRecord> personasAuth;
        List<PerfilLegalRecord> perfilesLegales;
        
        try {
            PaginatedResponse<UsuarioRecord> res = authClient.getUsuarios(null);
            personasAuth = res != null && res.items() != null ? res.items() : new ArrayList<>();
        } catch (Exception e) {
            personasAuth = new ArrayList<>();
        }
        
        try {
            perfilesLegales = backendClient.getPerfilesLegales(null);
        } catch (Exception e) {
            perfilesLegales = new ArrayList<>();
        }

        Map<Integer, PerfilLegalRecord> perfilesMap = new HashMap<>();
        if (perfilesLegales != null) {
            for (PerfilLegalRecord p : perfilesLegales) {
                if (p.usuarioId() != null) perfilesMap.put(p.usuarioId(), p);
            }
        }

        List<PersonaCompletaRecord> personasCompletas = new ArrayList<>();
        String query = q != null ? q.toLowerCase() : null;

        if (personasAuth != null) {
            for (UsuarioRecord u : personasAuth) {
                PerfilLegalRecord perfil = perfilesMap.get(u.id());

                String nombre = perfil != null ? perfil.nombre() : "";
                String apellido = perfil != null ? perfil.apellido() : "";
                String dni = perfil != null ? perfil.dni() : "";
                String telefono = perfil != null ? perfil.telefono() : "";
                String direccion = perfil != null ? perfil.direccion() : "";
                String fechaNacimiento = perfil != null ? perfil.fechaNacimiento() : "";

                PersonaCompletaRecord persona = new PersonaCompletaRecord(
                        u.id(), u.email(), u.username(), u.rol(),
                        nombre, apellido, dni, telefono, direccion, fechaNacimiento
                );

                if (rol != null && !rol.isEmpty() && !"ALL".equals(rol)) {
                    if (!String.valueOf(u.rol()).equals(rol)) continue;
                }

                if (query != null && !query.isEmpty()) {
                    String nombreLower = (nombre != null ? nombre : "").toLowerCase();
                    String apellidoLower = (apellido != null ? apellido : "").toLowerCase();
                    String emailLower = (u.email() != null ? u.email() : "").toLowerCase();
                    String usernameLower = (u.username() != null ? u.username() : "").toLowerCase();

                    if (!nombreLower.contains(query) && !apellidoLower.contains(query) &&
                            !emailLower.contains(query) && !usernameLower.contains(query)) {
                        continue;
                    }
                }
                personasCompletas.add(persona);
            }
        }

        int totalElements = personasCompletas.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        if (totalPages == 0) totalPages = 1;

        int activePage = page;
        if (activePage < 1) activePage = 1;
        if (activePage > totalPages) activePage = totalPages;

        int fromIndex = (activePage - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<PersonaCompletaRecord> paginatedItems = new ArrayList<>();
        if (fromIndex < totalElements && fromIndex >= 0) {
            paginatedItems = personasCompletas.subList(fromIndex, toIndex);
        }

        boolean hasNext = activePage < totalPages;
        boolean hasPrevious = activePage > 1;

        PaginatedResponse<PersonaCompletaRecord> pagination = new PaginatedResponse<>(
                paginatedItems, totalPages, totalElements, activePage, size, hasNext, hasPrevious
        );

        modelData.put("personas", paginatedItems);
        modelData.put("pagination", pagination);
        return modelData;
    }

    public List<UsuarioEncontradoRecord> buildBuscarSugerenciasModelData(String q, String context) {
        if (q == null || q.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<UsuarioRecord> usuarios;
        List<PerfilLegalRecord> perfiles;
        List<AdoptanteRecord> adoptantes;
        List<VoluntarioRecord> voluntarios;
        
        try {
            PaginatedResponse<UsuarioRecord> res = authClient.getUsuarios(1000);
            usuarios = res != null && res.items() != null ? res.items() : new ArrayList<>();
        } catch (Exception e) {
            usuarios = new ArrayList<>();
        }
        try { perfiles = backendClient.getPerfilesLegales(1000); } catch (Exception e) { perfiles = new ArrayList<>(); }
        try {
            PaginatedResponse<AdoptanteRecord> res = backendClient.getAdoptantes(1000);
            adoptantes = res != null && res.items() != null ? res.items() : new ArrayList<>();
        } catch (Exception e) {
            adoptantes = new ArrayList<>();
        }
        try {
            PaginatedResponse<VoluntarioRecord> res = backendClient.getVoluntarios(1000);
            voluntarios = res != null && res.items() != null ? res.items() : new ArrayList<>();
        } catch (Exception e) {
            voluntarios = new ArrayList<>();
        }
        
        Map<Integer, PerfilLegalRecord> perfilesMap = new HashMap<>();
        if (perfiles != null) {
            for (PerfilLegalRecord p : perfiles) {
                if (p.usuarioId() != null) perfilesMap.put(p.usuarioId(), p);
            }
        }

        Map<Integer, Integer> adoptantesUserIds = new HashMap<>();
        if (adoptantes != null) {
            for (AdoptanteRecord a : adoptantes) {
                if (a.usuarioId() != null) adoptantesUserIds.put(a.usuarioId(), a.id());
            }
        }

        Set<Integer> voluntariosUserIds = new HashSet<>();
        if (voluntarios != null) {
            for (VoluntarioRecord v : voluntarios) {
                if (v.usuarioId() != null) voluntariosUserIds.add(v.usuarioId());
            }
        }

        String query = q.toLowerCase().trim();
        List<UsuarioEncontradoRecord> encontrados = new ArrayList<>();

        if (usuarios != null) {
            for (UsuarioRecord u : usuarios) {
                PerfilLegalRecord perfil = perfilesMap.get(u.id());

                String nombre = perfil != null && perfil.nombre() != null ? perfil.nombre() : "";
                String apellido = perfil != null && perfil.apellido() != null ? perfil.apellido() : "";
                String email = u.email() != null ? u.email() : "";
                String username = u.username() != null ? u.username() : "";
                
                if (nombre.isEmpty() && apellido.isEmpty() && !username.isEmpty()) {
                    nombre = username;
                }
                
                String nombreCompleto = (nombre + " " + apellido).trim();
                String idStr = String.valueOf(u.id());

                boolean matches = nombre.toLowerCase().contains(query) ||
                                  apellido.toLowerCase().contains(query) ||
                                  email.toLowerCase().contains(query) ||
                                  username.toLowerCase().contains(query) ||
                                  nombreCompleto.toLowerCase().contains(query) ||
                                  idStr.equals(query);

                if (matches) {
                    if ("solicitud".equals(context) && !adoptantesUserIds.containsKey(u.id())) {
                        try {
                            Map<String, Object> adoptanteReq = new HashMap<>();
                            adoptanteReq.put("usuarioId", u.id());
                            adoptanteReq.put("estadoValidacion", "APROBADO");
                            
                            Map<String, Object> createdAdoptante = createAdoptante(adoptanteReq);
                            if (createdAdoptante != null && createdAdoptante.get("id") != null) {
                                Integer newAdoptanteId = ((Number) createdAdoptante.get("id")).intValue();
                                adoptantesUserIds.put(u.id(), newAdoptanteId);
                            }
                        } catch (Exception e) {}
                    }

                    boolean yaRegistrado = false;
                    if ("adoptante".equals(context)) {
                        yaRegistrado = adoptantesUserIds.containsKey(u.id());
                    } else if ("voluntario".equals(context)) {
                        yaRegistrado = voluntariosUserIds.contains(u.id());
                    } else if ("adopcion_filter".equals(context)) {
                        if (!adoptantesUserIds.containsKey(u.id())) {
                            continue;
                        }
                    }
                    
                    Integer adoptanteId = adoptantesUserIds.get(u.id());
                    encontrados.add(new UsuarioEncontradoRecord(
                            u.id(), username, email, u.rol(), nombre, apellido, adoptanteId, yaRegistrado
                    ));
                }
            }
        }
        return encontrados;
    }
}

