package es.refugio.refugio.application.usecase.adoptante;

import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.repository.AdoptanteRepository;

@AllArgsConstructor
public class CreateAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;

    public Adoptante create(CreateAdoptanteCommand comando) {
        Adoptante adoptante = Adoptante.builder()
                .usuarioId(comando.usuarioId())
                .dni(comando.dni())
                .direccion(comando.direccion())
                .fechaNacimiento(comando.fechaNacimiento())
                .estadoValidacion(EstadoValidacion.PENDIENTE) 

                .fechaRegistro(LocalDateTime.now())
                .solicitudesIds(new ArrayList<>())
                .adopcionesIds(new ArrayList<>())
                .build();

        return adoptanteRepository.save(adoptante);
    }
}