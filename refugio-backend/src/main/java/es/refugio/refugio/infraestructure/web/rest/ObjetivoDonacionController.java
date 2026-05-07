package es.refugio.refugio.infraestructure.web.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.refugio.refugio.application.usecase.donacion.CreateObjetivoDonacionUseCase;
import es.refugio.refugio.application.usecase.donacion.FindObjetivoDonacionUseCase;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacion;
import es.refugio.refugio.infraestructure.web.dto.donacion.ObjetivoDonacionRequest;
import es.refugio.refugio.infraestructure.web.dto.donacion.ObjetivoDonacionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/objetivos-donacion")
@RequiredArgsConstructor
@Tag(name = "Objetivos de Donación", description = "Endpoints para gestionar las necesidades y objetivos del refugio")
public class ObjetivoDonacionController {

    private final CreateObjetivoDonacionUseCase createUseCase;
    private final FindObjetivoDonacionUseCase findUseCase;

    @PostMapping
    public ResponseEntity<ObjetivoDonacionResponse> create(@RequestBody ObjetivoDonacionRequest request) {
        ObjetivoDonacion domain = ObjetivoDonacion.builder()
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .montoObjetivo(request.montoObjetivo())
                .montoRecaudado(request.montoRecaudado())
                .prioridad(request.prioridad())
                .estado(request.estado())
                .fechaLimite(request.fechaLimite())
                .icono(request.icono())
                .build();

        ObjetivoDonacion saved = createUseCase.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoDonacionResponse>> findAll() {
        List<ObjetivoDonacionResponse> list = findUseCase.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    private ObjetivoDonacionResponse toResponse(ObjetivoDonacion domain) {
        return new ObjetivoDonacionResponse(
                domain.getId() != null ? domain.getId().getValue() : null,
                domain.getTitulo(),
                domain.getDescripcion(),
                domain.getMontoObjetivo(),
                domain.getMontoRecaudado(),
                domain.getPrioridad() != null ? domain.getPrioridad().name() : null,
                domain.getEstado() != null ? domain.getEstado().name() : null,
                domain.getFechaInicio(),
                domain.getFechaLimite(),
                domain.getIcono());
    }
}
