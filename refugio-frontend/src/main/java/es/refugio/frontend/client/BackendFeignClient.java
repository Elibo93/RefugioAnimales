package es.refugio.frontend.client;

import es.refugio.frontend.web.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(name = "refugio-backend", path = "/api/v1")
public interface BackendFeignClient {

    // --- Animales ---
    @GetMapping("/animales")
    PaginatedResponse<AnimalRecord> getAnimales(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "especie", required = false) String especie,
            @RequestParam(value = "tamano", required = false) String tamano,
            @RequestParam(value = "edad", required = false) List<String> edad,
            @RequestParam(value = "sexo", required = false) String sexo,
            @RequestParam(value = "urgencia", required = false) Boolean urgencia,
            @RequestParam(value = "q", required = false) String q);

    @GetMapping("/animales")
    PaginatedResponse<AnimalRecord> getAllAnimales(@RequestParam(value = "size") int size);

    @GetMapping("/animales/{id}")
    AnimalRecord getAnimalById(@PathVariable("id") Integer id);

    @GetMapping("/animales/especies")
    List<String> getEspeciesActivas();

    @GetMapping("/animales/favoritos")
    List<Integer> getMisFavoritosIds(@RequestParam("usuarioId") Integer usuarioId);

    @GetMapping("/animales/{animalId}/favoritos/count")
    Integer countFavoritos(@PathVariable("animalId") Integer animalId);

    @PostMapping("/animales/{animalId}/favorito")
    Boolean toggleFavorito(@PathVariable("animalId") Integer animalId, @RequestParam("usuarioId") Integer usuarioId);

    @PostMapping("/animales/{animalId}/visitas")
    void registrarVisita(@PathVariable("animalId") Integer animalId);

    @PostMapping(value = "/animales", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void crearAnimal(@RequestPart("animal") Map<String, Object> animal, @RequestPart(value = "fotoArchivo", required = false) MultipartFile fotoArchivo);

    @PutMapping(value = "/animales/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void editarAnimal(@PathVariable("id") Integer id, @RequestPart("animal") Map<String, Object> animal, @RequestPart(value = "fotoArchivo", required = false) MultipartFile fotoArchivo);

    @DeleteMapping("/animales/{id}")
    void eliminarAnimal(@PathVariable("id") Integer id);

    // --- Perfiles Legales ---
    @PostMapping("/perfiles-legales")
    void createPerfilLegal(@RequestBody Map<String, Object> legalBody);

    @GetMapping("/perfiles-legales")
    List<PerfilLegalRecord> getPerfilesLegales(@RequestParam(value = "size", required = false) Integer size);

    @GetMapping("/perfiles-legales/usuario/{usuarioId}")
    PerfilLegalRecord getPerfilLegalByUsuarioId(@PathVariable("usuarioId") Integer usuarioId);

    @DeleteMapping("/perfiles-legales/usuario/{id}")
    void deletePerfilLegal(@PathVariable("id") Integer id);

    // --- Adoptantes y Voluntarios ---
    @PostMapping("/adoptantes")
    Map<String, Object> createAdoptante(@RequestBody Map<String, Object> adoptanteReq);

    @GetMapping("/adoptantes")
    PaginatedResponse<AdoptanteRecord> getAdoptantesPaginated(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam(value = "q", required = false) String q);

    @GetMapping("/adoptantes")
    PaginatedResponse<AdoptanteRecord> getAdoptantes(@RequestParam("size") Integer size);

    @GetMapping("/adoptantes/{id}")
    AdoptanteRecord getAdoptanteById(@PathVariable("id") Integer id);

    @GetMapping("/adoptantes/usuario/{usuarioId}")
    AdoptanteRecord getAdoptanteByUsuarioId(@PathVariable("usuarioId") Integer usuarioId);

    @PutMapping("/adoptantes/{id}")
    void updateAdoptante(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body);

    @DeleteMapping("/adoptantes/{id}")
    void deleteAdoptante(@PathVariable("id") Integer id);

    @PatchMapping("/adoptantes/{id}/approve")
    void approveAdoptante(@PathVariable("id") Integer id);

    @PatchMapping("/adoptantes/{id}/reject")
    void rejectAdoptante(@PathVariable("id") Integer id);

    @PostMapping("/voluntarios")
    Map<String, Object> createVoluntario(@RequestBody Map<String, Object> body);

    @GetMapping("/voluntarios")
    PaginatedResponse<VoluntarioRecord> getVoluntariosPaginated(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "excludeTareaId", required = false) Integer excludeTareaId,
            @RequestParam(value = "excludeDate", required = false) String excludeDate);

    @GetMapping("/voluntarios")
    PaginatedResponse<VoluntarioRecord> getVoluntarios(@RequestParam("size") Integer size);

    @GetMapping("/voluntarios/{id}")
    VoluntarioRecord getVoluntarioById(@PathVariable("id") Integer id);

    @PutMapping("/voluntarios/{id}")
    void updateVoluntario(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body);

    @DeleteMapping("/voluntarios/{id}")
    void deleteVoluntario(@PathVariable("id") Integer id);

    @PostMapping("/voluntarios/{id}/aprobar")
    void aprobarVoluntario(@PathVariable("id") Integer id);

    @PostMapping("/voluntarios/{id}/rechazar")
    void rechazarVoluntario(@PathVariable("id") Integer id);

    @GetMapping("/voluntarios/pendientes")
    List<VoluntarioRecord> getVoluntariosPendientes(@RequestParam(value = "size", defaultValue = "1000") Integer size);

    @GetMapping("/voluntarios/usuario/{usuarioId}")
    VoluntarioRecord getVoluntarioByUsuarioId(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping("/voluntarios/{id}/disponibilidad")
    List<Map<String, Object>> getDisponibilidad(@PathVariable("id") Integer id);

    @PostMapping("/voluntarios/{id}/disponibilidad")
    void addDisponibilidad(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body);

    @DeleteMapping("/voluntarios/{id}/disponibilidad/{disponibilidadId}")
    void deleteDisponibilidad(@PathVariable("id") Integer id, @PathVariable("disponibilidadId") Integer disponibilidadId);

    // --- Tareas ---
    @GetMapping("/tareas")
    PaginatedResponse<TareaRecord> getTareas(@RequestParam(value = "size", defaultValue = "9999") Integer size);

    @GetMapping("/tareas/{id}")
    TareaRecord getTareaById(@PathVariable("id") Integer id);

    @PostMapping("/tareas")
    void createTarea(@RequestBody Map<String, Object> body);

    @PutMapping("/tareas/{id}")
    void updateTarea(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body);

    @DeleteMapping("/tareas/{id}")
    void deleteTarea(@PathVariable("id") Integer id);

    @GetMapping("/reports/tarea/{id}")
    ResponseEntity<byte[]> descargarPdfTarea(@PathVariable("id") Integer id);

    @GetMapping("/tareas/{id}/html")
    String getHtmlTarea(@PathVariable("id") Integer id);

    @GetMapping("/tareas/{id}/historial")
    List<Map<String, Object>> getHistorialTarea(@PathVariable("id") Integer id);

    // --- Solicitudes Adopcion ---
    @GetMapping("/solicitudes-adopcion")
    PaginatedResponse<SolicitudAdopcionRecord> getSolicitudesAdopcionPaginated(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size);

    @GetMapping("/solicitudes-adopcion")
    PaginatedResponse<SolicitudAdopcionRecord> getSolicitudesAdopcion(@RequestParam("size") Integer size);

    @GetMapping("/solicitudes-adopcion/{id}")
    SolicitudAdopcionRecord getSolicitudAdopcionById(@PathVariable("id") Integer id);

    @GetMapping("/solicitudes-adopcion/adoptante/{adoptanteId}")
    List<SolicitudAdopcionRecord> getSolicitudesAdopcionByAdoptanteId(@PathVariable("adoptanteId") Integer adoptanteId);

    @PostMapping("/solicitudes-adopcion")
    void createSolicitudAdopcion(@RequestBody Map<String, Object> body);

    @PutMapping("/solicitudes-adopcion/{id}")
    void updateSolicitudAdopcion(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body);

    @DeleteMapping("/solicitudes-adopcion/{id}")
    void deleteSolicitudAdopcion(@PathVariable("id") Integer id);

    @PostMapping("/solicitudes-adopcion/{id}/aprobar")
    void aprobarSolicitud(@PathVariable("id") Integer id);

    @PostMapping("/solicitudes-adopcion/{id}/rechazar")
    void rechazarSolicitud(@PathVariable("id") Integer id);

    @PostMapping("/solicitudes-adopcion/convertir-y-adopcion")
    void convertirYAdopcion(@RequestBody Map<String, Object> body);

    @PostMapping("/solicitudes-adopcion/directa")
    void crearAdopcionDirecta(@RequestBody Map<String, Object> body);

    @PostMapping("/solicitudes-adopcion/publico/registro-y-adopcion")
    void registrarYAdopcionPublico(@RequestBody Map<String, Object> body);

    // --- Adopciones ---
    @GetMapping("/adopciones")
    PaginatedResponse<AdopcionRecord> getAdopcionesPaginated(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "estado", required = false) String estado);

    @GetMapping("/adopciones")
    PaginatedResponse<AdopcionRecord> getAdopciones(@RequestParam("size") Integer size);

    @GetMapping("/adopciones/{id}")
    AdopcionRecord getAdopcionById(@PathVariable("id") Integer id);

    @PostMapping("/adopciones")
    void createAdopcion(@RequestBody Map<String, Object> body);

    @PutMapping("/adopciones/{id}")
    void updateAdopcion(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body);

    @DeleteMapping("/adopciones/{id}")
    void deleteAdopcion(@PathVariable("id") Integer id);

    @GetMapping("/adopciones/adoptante/{adoptanteId}")
    List<AdopcionRecord> getAdopcionesByAdoptanteId(@PathVariable("adoptanteId") Integer adoptanteId, @RequestParam(value = "size") Integer size);

    @GetMapping("/adopciones/animal/{animalId}")
    List<AdopcionRecord> getAdopcionesByAnimalId(@PathVariable("animalId") Integer animalId, @RequestParam(value = "size") Integer size);

    @GetMapping("/reports/solicitud/{id}")
    ResponseEntity<byte[]> descargarPdfSolicitud(@PathVariable("id") Integer id);

    @GetMapping("/reports/adopcion/{id}/contrato")
    ResponseEntity<byte[]> descargarContrato(@PathVariable("id") Integer id);

    // --- Donaciones y Objetivos ---
    @GetMapping("/donaciones")
    PaginatedResponse<DonacionRecord> getDonacionesPaginated(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size);

    @GetMapping("/donaciones")
    PaginatedResponse<DonacionRecord> getDonaciones(@RequestParam("size") Integer size);

    @GetMapping("/donaciones/{id}")
    DonacionRecord getDonacionById(@PathVariable("id") Integer id);

    @GetMapping("/donaciones/total")
    Double getTotalDineroDonaciones();

    @PostMapping("/donaciones")
    void createDonacion(@RequestBody Map<String, Object> body);

    @PutMapping("/donaciones/{id}")
    void updateDonacion(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body);

    @DeleteMapping("/donaciones/{id}")
    void deleteDonacion(@PathVariable("id") Integer id);

    @GetMapping("/objetivos-donacion")
    List<Map<String, Object>> getObjetivosDonacion(@RequestParam(value = "size") Integer size);

    @PostMapping("/objetivos-donacion")
    void createObjetivoDonacion(@RequestBody Map<String, Object> body);

    // --- Gamificación ---
    @GetMapping("/gamificacion/metricas/usuario/{id}")
    UsuarioMetricasRecord fetchMetricasGamificacion(@PathVariable("id") Integer id);

    @GetMapping("/gamificacion/logros/usuario/{id}")
    List<Map<String, Object>> getLogrosUsuario(@PathVariable("id") Integer id);

    @GetMapping("/gamificacion/logros")
    List<Map<String, Object>> getTodosLosLogros();

    // --- Historial Médico ---
    @GetMapping("/historial-medico")
    PaginatedResponse<HistorialMedicoRecord> getHistorialMedicoPaginated(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size);

    @GetMapping("/historial-medico")
    PaginatedResponse<HistorialMedicoRecord> getHistorialMedico(@RequestParam("size") Integer size);

    @GetMapping("/historial-medico/{id}")
    HistorialMedicoRecord getHistorialMedicoById(@PathVariable("id") Integer id);

    @GetMapping("/historial-medico/animal/{animalId}")
    List<HistorialMedicoRecord> getHistorialMedicoByAnimalId(@PathVariable("animalId") Integer animalId, @RequestParam(value = "size") Integer size);

    @PostMapping("/historial-medico")
    void createHistorialMedico(@RequestBody Map<String, Object> body);

    @PutMapping("/historial-medico/{id}")
    void updateHistorialMedico(@PathVariable("id") Integer id, @RequestBody Map<String, Object> body);

    @DeleteMapping("/historial-medico/{id}")
    void deleteHistorialMedico(@PathVariable("id") Integer id);

    // --- Notificaciones ---
    @GetMapping("/notificaciones/me")
    List<Map<String, Object>> getMisNotificaciones();

    @PutMapping("/notificaciones/{id}/leer")
    void marcarNotificacionLeida(@PathVariable("id") Integer id);

    @GetMapping("/notificaciones/me/count")
    Long countNotificacionesNoLeidas();

    @DeleteMapping("/notificaciones/{id}")
    void deleteNotificacion(@PathVariable("id") Integer id);

    // --- Global Attributes ---
    @GetMapping("/solicitudes-adopcion/mis-solicitudes")
    List<Map<String, Object>> getMisSolicitudes();

    @GetMapping("/preferencias/usuario/{userId}")
    Map<String, Object> getPreferenciasByUsuarioId(@PathVariable("userId") Integer userId);

    @GetMapping("/solicitudes-adopcion/count/pendiente")
    Long countSolicitudesPendientes();

    @GetMapping("/voluntarios/count/pendiente")
    Long countVoluntariosPendientes();

    @PostMapping("/preferencias")
    void guardarPreferencias(@RequestBody Map<String, Object> body);

    // --- Otros endpoints se irán añadiendo según se necesiten ---
}
