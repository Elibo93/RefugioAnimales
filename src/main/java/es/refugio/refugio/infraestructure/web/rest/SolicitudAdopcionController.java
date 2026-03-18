package es.refugio.refugio.infraestructure.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.application.service.solicitud_adopcion.CreateSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.DeleteSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.EditSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.FindSolicitudAdopcionService;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.infraestructure.mapper.SolicitudAdopcionMapper;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionRequest;
import es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion.SolicitudAdopcionResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/solicitudes-adopcion")
@RequiredArgsConstructor
public class SolicitudAdopcionController {

    private final CreateSolicitudAdopcionService createSolicitudAdopcionService;
    private final FindSolicitudAdopcionService findSolicitudAdopcionService;
    private final EditSolicitudAdopcionService editSolicitudAdopcionService;
    private final DeleteSolicitudAdopcionService deleteSolicitudAdopcionService;

    @PostMapping
    public ResponseEntity<SolicitudAdopcionResponse> createSolicitudAdopcion(@Valid @RequestBody SolicitudAdopcionRequest request) {
        CreateSolicitudAdopcionCommand command = SolicitudAdopcionMapper.toCommand(request);
        SolicitudAdopcion solicitud = createSolicitudAdopcionService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitudAdopcionMapper.toResponse(solicitud));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudAdopcionResponse> updateSolicitudAdopcion(@PathVariable Integer id,
                                                                             @Valid @RequestBody SolicitudAdopcionRequest request) {
        EditSolicitudAdopcionCommand command = SolicitudAdopcionMapper.toCommand(id, request);
        SolicitudAdopcion solicitud = editSolicitudAdopcionService.update(command);
        return ResponseEntity.ok(SolicitudAdopcionMapper.toResponse(solicitud));
    }

    @GetMapping
    public List<SolicitudAdopcionResponse> getAll() {
        return findSolicitudAdopcionService.findAll()
                .stream()
                .map(SolicitudAdopcionMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public SolicitudAdopcionResponse getSolicitudAdopcionById(@PathVariable Integer id) {
        return SolicitudAdopcionMapper.toResponse(findSolicitudAdopcionService.findById(new SolicitudAdopcionId(id)));
    }

    @GetMapping("/animal/{animalId}")
    public List<SolicitudAdopcionResponse> getSolicitudAdopcionByAnimalId(@PathVariable Integer animalId) {
        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService.findByAnimalId(new AnimalId(animalId));
        return SolicitudAdopcionMapper.toResponse(solicitudes);
    }

    @GetMapping("/adoptante/{adoptanteId}")
    public List<SolicitudAdopcionResponse> getSolicitudAdopcionByAdoptanteId(@PathVariable Integer adoptanteId) {
        List<SolicitudAdopcion> solicitudes = findSolicitudAdopcionService.findByAdoptanteId(new AdoptanteId(adoptanteId));
        return SolicitudAdopcionMapper.toResponse(solicitudes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitudAdopcion(@PathVariable Integer id) {
        deleteSolicitudAdopcionService.delete(new SolicitudAdopcionId(id));
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return errors;
    }
}
