package es.refugio.animales.refugio.infraestructure.web.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.refugio.animales.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.animales.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.animales.refugio.application.service.voluntario.CreateVoluntarioService;
import es.refugio.animales.refugio.application.service.voluntario.DeleteVoluntarioService;
import es.refugio.animales.refugio.application.service.voluntario.EditVoluntarioService;
import es.refugio.animales.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
import es.refugio.animales.refugio.infraestructure.mapper.VoluntarioMapper;
import es.refugio.animales.refugio.infraestructure.web.dto.voluntario.VoluntarioRequest;
import es.refugio.animales.refugio.infraestructure.web.dto.voluntario.VoluntarioResponse;
import es.refugio.animales.refugio.domain.model.voluntario.VoluntarioId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/voluntarios")
@RequiredArgsConstructor
public class VoluntarioController {

    private final CreateVoluntarioService createVoluntarioService;
    private final FindVoluntarioService findVoluntarioService;
    private final DeleteVoluntarioService deleteVoluntarioService;
    private final EditVoluntarioService editVoluntarioService;

    @PostMapping
    public ResponseEntity<VoluntarioResponse> createVoluntario(
            @Valid @RequestBody VoluntarioRequest VoluntarioRequest) {
        CreateVoluntarioCommand comando = VoluntarioMapper.toCommand(VoluntarioRequest);
        Voluntario voluntario = createVoluntarioService.createVoluntario(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(VoluntarioMapper.toResponse(voluntario));
    }

    @GetMapping
    public List<VoluntarioResponse> getAll() {

        return findVoluntarioService.findAll()
                .stream()
                .map(VoluntarioMapper::toResponse)
                .toList();

    }

    @PutMapping("/{id}")
    public VoluntarioResponse editVoluntario(@PathVariable int id,
            @Valid @RequestBody VoluntarioRequest VoluntarioRequest) {
        EditVoluntarioCommand comando = VoluntarioMapper.toCommand(id, VoluntarioRequest);
        Voluntario voluntario = editVoluntarioService.update(comando);
        return VoluntarioMapper.toResponse(voluntario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoluntario(@PathVariable int id) {
        deleteVoluntarioService.delete(new VoluntarioId(id));
        return ResponseEntity.noContent().build();
    }
}
