package es.refugio.refugio.application.service.voluntario;

import java.util.ArrayList;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import es.refugio.refugio.application.command.voluntario.SetDisponibilidadCommand;
import java.time.LocalDate;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.model.voluntario.DisponibilidadVoluntario;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Set Disponibilidad.
 *
 * @author Elisabeth
 * @author Diego
 */
public class SetDisponibilidadService {
    
    private final VoluntarioRepository voluntarioRepository;
    
    public Voluntario setDisponibilidad(SetDisponibilidadCommand command) {
        Voluntario voluntario = voluntarioRepository.getById(command.voluntarioId())
                .orElseThrow(() -> new RuntimeException("Voluntario no encontrado"));
                
        if (voluntario.getDisponibilidades() == null) {
            voluntario.setDisponibilidades(new ArrayList<>());
        }
        
        Optional<DisponibilidadVoluntario> existente = voluntario.getDisponibilidades().stream()
                .filter(d -> d.getFecha().equals(command.fecha()))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setTurno(command.turno());
            existente.get().setEstado(command.estado());
        } else {
            DisponibilidadVoluntario nuevaDisponibilidad = DisponibilidadVoluntario.builder()
                    .voluntarioId(command.voluntarioId())
                    .fecha(command.fecha())
                    .turno(command.turno())
                    .estado(command.estado())
                    .build();
            voluntario.getDisponibilidades().add(nuevaDisponibilidad);
        }
        
        return voluntarioRepository.save(voluntario);
    }

    public Voluntario deleteDisponibilidad(VoluntarioId id, LocalDate fecha) {
        Voluntario voluntario = voluntarioRepository.getById(id)
                .orElseThrow(() -> new RuntimeException("Voluntario no encontrado"));
                
        if (voluntario.getDisponibilidades() != null) {
            voluntario.getDisponibilidades().removeIf(d -> d.getFecha().equals(fecha));
            return voluntarioRepository.save(voluntario);
        }
        return voluntario;
    }
}
