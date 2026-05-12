package es.refugio.refugio.application.service.preferencia;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.infraestructure.db.jpa.entity.PreferenciaAdopcionEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.preferencia.JpaPreferenciaAdopcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final JpaPreferenciaAdopcionRepository repository;
    private final NotificacionService notificacionService;

    public void processNewAnimal(Animal animal) {
        log.info("Iniciando proceso de matching para nuevo animal: {} ({})", animal.getNombre(), animal.getEspecie());
        
        List<PreferenciaAdopcionEntity> todasLasPreferencias = repository.findByNotificacionesActivasTrue();
        
        for (PreferenciaAdopcionEntity pref : todasLasPreferencias) {
            if (isMatch(animal, pref)) {
                notificacionService.enviar(
                    pref.getUsuarioId(),
                    "¡Nuevo Match! " + animal.getNombre() + " te está esperando",
                    "Un nuevo animal que encaja con tus preferencias ha llegado al refugio: " + animal.getNombre() + " (" + animal.getEspecie() + ")",
                    "MATCH",
                    "/web/animales" // Enlace a la lista o detalle
                );
            }
        }
    }

    private boolean isMatch(Animal animal, PreferenciaAdopcionEntity pref) {
        // 1. Especie (Si el usuario eligió especies y el animal no está en la lista -> false)
        if (pref.getEspecies() != null && !pref.getEspecies().isEmpty()) {
            if (!pref.getEspecies().contains(animal.getEspecie())) {
                return false;
            }
        }

        // 2. Tamaño
        if (pref.getTamanos() != null && !pref.getTamanos().isEmpty()) {
            if (animal.getTamano() != null && !pref.getTamanos().contains(animal.getTamano())) {
                return false;
            }
        }

        // 3. Sexo
        if (pref.getSexos() != null && !pref.getSexos().isEmpty()) {
            if (animal.getSexo() != null && !pref.getSexos().contains(animal.getSexo())) {
                return false;
            }
        }

        // 4. Edad Máxima
        if (pref.getEdadMax() != null) {
            if (animal.getEdad() != null && animal.getEdad() > pref.getEdadMax()) {
                return false;
            }
        }

        // 5. Energía Máxima
        if (pref.getNivelEnergiaMax() != null) {
            if (animal.getNivelEnergia() != null && animal.getNivelEnergia() > pref.getNivelEnergiaMax()) {
                return false;
            }
        }

        return true;
    }
}
