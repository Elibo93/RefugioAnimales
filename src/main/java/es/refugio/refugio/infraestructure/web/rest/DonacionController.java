package es.refugio.refugio.infraestructure.web.rest;

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

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.application.command.donacion.EditDonacionCommand;
import es.refugio.refugio.application.service.donacion.CreateDonacionService;
import es.refugio.refugio.application.service.donacion.DeleteDonacionService;
import es.refugio.refugio.application.service.donacion.EditDonacionService;
import es.refugio.refugio.application.service.donacion.FindDonacionService;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.infraestructure.mapper.DonacionMapper;
import es.refugio.refugio.infraestructure.web.dto.donacion.DonacionRequest;
import es.refugio.refugio.infraestructure.web.dto.donacion.DonacionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/donaciones")
@RequiredArgsConstructor
public class DonacionController {

    private final CreateDonacionService createDonacionService;
    private final FindDonacionService findDonacionService;
    private final EditDonacionService editDonacionService;
    private final DeleteDonacionService deleteDonacionService;

    @PostMapping
    public ResponseEntity<DonacionResponse> create(@Valid @RequestBody DonacionRequest request) {
        CreateDonacionCommand command = DonacionMapper.toCommand(request);
        Donacion donacion = createDonacionService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(DonacionMapper.toResponse(donacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonacionResponse> update(@PathVariable Integer id, @Valid @RequestBody DonacionRequest request) {
        EditDonacionCommand command = DonacionMapper.toCommand(id, request);
        Donacion donacion = editDonacionService.update(command);
        return ResponseEntity.ok(DonacionMapper.toResponse(donacion));
    }

    @GetMapping
    public List<DonacionResponse> findAll() {
        return DonacionMapper.toResponse(findDonacionService.findAll());
    }

    @GetMapping("/{id}")
    public DonacionResponse findById(@PathVariable Integer id) {
        return DonacionMapper.toResponse(findDonacionService.findById(new DonacionId(id)));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<DonacionResponse> findByUsuarioId(@PathVariable Integer usuarioId) {
        return DonacionMapper.toResponse(findDonacionService.findByUsuarioId(new UsuarioId(usuarioId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteDonacionService.delete(new DonacionId(id));
        return ResponseEntity.noContent().build();
    }
}
