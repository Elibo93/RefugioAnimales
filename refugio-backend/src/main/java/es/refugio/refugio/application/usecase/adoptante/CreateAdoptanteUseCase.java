package es.refugio.refugio.application.usecase.adoptante;

import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

@AllArgsConstructor
public class CreateAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;

    public Adoptante create(CreateAdoptanteCommand comando) {
        var byUsuario = adoptanteRepository.getByUsuarioId(new UsuarioId(comando.usuarioId()));
        if (byUsuario.isPresent()) {
            return byUsuario.get();
        }

        // Evitar duplicidad de DNI
        var byDni = adoptanteRepository.getByDni(comando.dni());
        if (byDni.isPresent()) {
            throw new RuntimeException("El DNI " + comando.dni() + " ya está registrado con otra cuenta de usuario.");
        }

        // Crear adoptante
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