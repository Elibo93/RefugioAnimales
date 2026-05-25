package es.refugio.refugio.application.usecase.adoptante;

import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

@AllArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Create Adoptante.
 *
 * @author Elisabeth
 * @author Diego
 */
public class CreateAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;
    private final PerfilLegalRepository perfilLegalRepository;

    public Adoptante create(CreateAdoptanteCommand comando, boolean isAdmin) {
        // 1. Verificar si ya es adoptante (Idempotencia)
        var byUsuario = adoptanteRepository.getByUsuarioId(new UsuarioId(comando.usuarioId()));
        if (byUsuario.isPresent()) {
            return byUsuario.get();
        }

        // 2. Verificar PerfilLegal (Identidad)
        perfilLegalRepository.findByUsuarioId(comando.usuarioId())
                .orElseThrow(() -> new IllegalStateException("error.adoptante.perfil_incompleto"));

        // Crear adoptante
        Adoptante adoptante = Adoptante.builder()
                .usuarioId(comando.usuarioId())
                .estadoValidacion(isAdmin ? EstadoValidacion.APROBADO : EstadoValidacion.PENDIENTE)
                .fechaRegistro(LocalDateTime.now())
                .solicitudesIds(new ArrayList<>())
                .adopcionesIds(new ArrayList<>())
                .build();

        return adoptanteRepository.save(adoptante);
    }
}