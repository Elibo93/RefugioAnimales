package es.refugio.frontend.service;

import es.refugio.frontend.client.AuthFeignClient;
import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Solicitudes de Adopción en el Frontend.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class SolicitudAdopcionService {

    private final BackendFeignClient backendClient;
    private final AuthFeignClient authClient;

    public PaginatedResponse<SolicitudAdopcionRecord> fetchPaginatedSolicitudes(int page, int size) {
        try {
            return backendClient.getSolicitudesAdopcionPaginated(page - 1, size);
        } catch (Exception e) {
            return new PaginatedResponse<>(List.of(), 0, 0, page, size, false, false);
        }
    }

    public List<SolicitudAdopcionRecord> fetchAllSolicitudes() {
        try {
            PaginatedResponse<SolicitudAdopcionRecord> res = backendClient.getSolicitudesAdopcion(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public SolicitudAdopcionRecord fetchSolicitudById(Integer id) {
        try {
            return backendClient.getSolicitudAdopcionById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public void crearSolicitud(Integer animalId, Integer adoptanteId, String estado, String comentario, String comentarioAdmin, LocalDateTime fecha) {
        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("estado", estado);
        body.put("comentario", comentario);
        body.put("comentarioAdmin", comentarioAdmin);
        body.put("fecha", fecha != null ? fecha.toString() : LocalDateTime.now().toString());
        
        backendClient.createSolicitudAdopcion(body);
    }

    public void editarSolicitud(Integer id, Integer animalId, Integer adoptanteId, String estado, String comentario, String comentarioAdmin, LocalDateTime fecha) {
        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("estado", estado);
        body.put("comentario", comentario);
        body.put("comentarioAdmin", comentarioAdmin);
        if (fecha != null) {
            body.put("fecha", fecha.toString());
        }
        
        backendClient.updateSolicitudAdopcion(id, body);
    }

    public void actualizarEstadoSolicitud(Integer id, String estado) {
        SolicitudAdopcionRecord record = fetchSolicitudById(id);
        if (record != null) {
            editarSolicitud(id, record.animalId(), record.adoptanteId(), estado, record.comentario(), record.comentarioAdmin(), record.fecha());
        }
    }

    public void eliminarSolicitud(Integer id) {
        backendClient.deleteSolicitudAdopcion(id);
    }

    public void aprobarSolicitud(Integer id) {
        backendClient.aprobarSolicitud(id);
    }

    public void rechazarSolicitud(Integer id) {
        backendClient.rechazarSolicitud(id);
    }

    public List<AnimalRecord> fetchAllAnimales() {
        try {
            return backendClient.getAllAnimales(1000).items();
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

    public List<AdoptanteRecord> fetchAllAdoptantes() {
        try {
            PaginatedResponse<AdoptanteRecord> res = backendClient.getAdoptantes(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public AdoptanteRecord fetchAdoptanteById(Integer id) {
        try {
            return backendClient.getAdoptanteById(id);
        } catch (Exception e) {
            return null;
        }
    }
    
    public AdoptanteRecord fetchAdoptanteByUsuarioId(Integer usuarioId) {
        try {
            return backendClient.getAdoptanteByUsuarioId(usuarioId);
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

    public List<AdopcionRecord> fetchAllAdopciones() {
        try {
            PaginatedResponse<AdopcionRecord> res = backendClient.getAdopciones(1000);
            return res != null && res.items() != null ? res.items() : List.of();
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

    public ResponseEntity<byte[]> descargarPdfSolicitud(Integer id) {
        return backendClient.descargarPdfSolicitud(id);
    }

    public UsuarioRecord fetchMe() {
        try {
            return authClient.getMe();
        } catch (Exception e) {
            return null;
        }
    }

    public void convertirYAdopcion(Map<String, Object> bodySolicitud) {
        backendClient.convertirYAdopcion(bodySolicitud);
    }

    public ResponseEntity<Map<String, Object>> actualizarRolUsuario(Integer usuarioId, String nuevoRol) {
        Map<String, String> patchBody = new HashMap<>();
        patchBody.put("rol", nuevoRol);
        return authClient.actualizarRolUsuario(usuarioId, patchBody);
    }

    public void crearAdopcionDirecta(Map<String, Object> body) {
        backendClient.crearAdopcionDirecta(body);
    }

    public Map<String, Object> registrarUsuarioPublico(Map<String, Object> userBody) {
        return authClient.registrarUsuario(userBody);
    }

    public void registrarYAdopcionPublico(Map<String, Object> bodySolicitud) {
        backendClient.registrarYAdopcionPublico(bodySolicitud);
    }

    @Value("${refugio.internal.secret}")
    private String internalSecret;

    public void eliminarUsuarioAuth(Integer id) {
        authClient.rollbackUsuarioAuth(id, internalSecret);
    }

    public ResponseEntity<String> loginPost(String email, String password) {
        return authClient.login(email, password, internalSecret);
    }

    public Map<String, Object> buildListarModelData(int page, int size) {
        Map<String, Object> modelData = new HashMap<>();
        PaginatedResponse<SolicitudAdopcionRecord> pagination = fetchPaginatedSolicitudes(page, size);
        List<SolicitudAdopcionRecord> solicitudes = pagination.items();
        
        List<SolicitudAdopcionRecord> pendientes = solicitudes.stream()
                .filter(s -> "PENDIENTE".equals(s.estado()) || "EN_REVISION".equals(s.estado()))
                .toList();

        List<AnimalRecord> animales = fetchAllAnimales();
        List<AdoptanteRecord> adoptantes = fetchAllAdoptantes();
        
        Map<String, AnimalRecord> animalesMap = new HashMap<>();
        for (AnimalRecord a : animales) {
            animalesMap.put(String.valueOf(a.id()), a);
        }

        Map<String, String> adoptanteNombres = new HashMap<>();
        Map<String, String> adoptanteUsuarioIds = new HashMap<>();
        
        for (AdoptanteRecord a : adoptantes) {
            if (a.usuarioId() != null) {
                adoptanteUsuarioIds.put(String.valueOf(a.id()), a.usuarioId().toString());
                try {
                    PerfilLegalRecord perfil = fetchPerfilLegalByUsuarioId(a.usuarioId());
                    if (perfil != null) {
                        String nombre = perfil.nombre() != null ? perfil.nombre() : "";
                        String apellido = perfil.apellido() != null ? perfil.apellido() : "";
                        adoptanteNombres.put(String.valueOf(a.id()), (nombre + " " + apellido).trim());
                    }
                } catch (Exception e) {}
            }
        }

        Map<String, String> solicitudToAdopcionMap = new HashMap<>();
        try {
            List<AdopcionRecord> allAdopciones = fetchAllAdopciones();
            if (allAdopciones != null && solicitudes != null) {
                for (SolicitudAdopcionRecord s : solicitudes) {
                    if ("APROBADA".equals(s.estado())) {
                        String key = s.adoptanteId() + "_" + s.animalId();
                        allAdopciones.stream()
                                .filter(ad -> key.equals(ad.adoptanteId() + "_" + ad.animalId()))
                                .findFirst()
                                .ifPresent(ad -> solicitudToAdopcionMap.put(String.valueOf(s.id()), String.valueOf(ad.id())));
                    }
                }
            }
        } catch (Exception e) {}

        modelData.put("solicitudList", solicitudes);
        modelData.put("pagination", pagination);
        modelData.put("pendientes", pendientes);
        modelData.put("animalesMap", animalesMap);
        modelData.put("adoptanteNombres", adoptanteNombres);
        modelData.put("adoptanteUsuarioIds", adoptanteUsuarioIds);
        modelData.put("solicitudToAdopcionMap", solicitudToAdopcionMap);
        return modelData;
    }

    public Map<String, Object> buildMisAdoptadosModelData(Integer currentUserId) {
        Map<String, Object> modelData = new HashMap<>();
        Integer adoptanteId = null;
        try {
            AdoptanteRecord adoptante = fetchAdoptanteByUsuarioId(currentUserId);
            if (adoptante != null) adoptanteId = adoptante.id();
        } catch (Exception e) {}

        List<SolicitudAdopcionRecord> todas = fetchAllSolicitudes();
        List<SolicitudAdopcionRecord> misSolicitudes = new ArrayList<>();

        if (adoptanteId != null) {
            for (SolicitudAdopcionRecord s : todas) {
                if (s.adoptanteId() != null && s.adoptanteId().equals(adoptanteId)) {
                    misSolicitudes.add(s);
                }
            }
            try {
                List<AdopcionRecord> adopciones = fetchAdopcionesByAdoptanteId(adoptanteId);
                if (adopciones != null) {
                    for (AdopcionRecord adopcion : adopciones) {
                        if (adopcion.solicitudAdopcionId() == null) {
                            SolicitudAdopcionRecord fakeSolicitud = new SolicitudAdopcionRecord(
                                    -adopcion.id(), adopcion.animalId(), adopcion.adoptanteId(),
                                    adopcion.fechaAdopcion(), "APROBADA", "Adopción directa", "");
                            misSolicitudes.add(fakeSolicitud);
                        }
                    }
                }
            } catch (Exception e) {}
        }
        
        if (!misSolicitudes.isEmpty()) {
            List<AnimalRecord> animales = fetchAllAnimales();
            Map<String, AnimalRecord> animalesMap = new HashMap<>();
            for (AnimalRecord a : animales) animalesMap.put(String.valueOf(a.id()), a);
            modelData.put("animalesMap", animalesMap);
        }
        
        modelData.put("solicitudList", misSolicitudes);
        return modelData;
    }
}

