package es.refugio.refugio.application.service.preferencia;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.infraestructure.db.jpa.entity.PreferenciaAdopcionEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.preferencia.JpaPreferenciaAdopcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación que evalúa la compatibilidad entre nuevos animales
 * y las preferencias de adopción registradas por los usuarios, enviando
 * notificaciones en tiempo real a los potenciales adoptantes interesados.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final JpaPreferenciaAdopcionRepository repository;
    private final NotificacionService notificacionService;

    /**
     * Inicia el proceso de matching para un animal recién registrado.
     * Recorre todas las preferencias de adopción activas y, si el animal
     * cumple los criterios de una preferencia, envía una notificación al
     * usuario propietario de esa preferencia.
     *
     * @param animal El animal recién creado sobre el que se ejecuta el matching.
     */
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

    /**
     * Evalúa si un animal cumple todos los criterios de una preferencia de adopción.
     * Aplica filtros secuenciales de especie, tamaño, sexo, edad máxima y nivel de energía.
     * Si el usuario no ha especificado un criterio, ese criterio no se aplica (es inclusivo).
     *
     * @param animal El animal a evaluar.
     * @param pref   La preferencia de adopción del usuario contra la que se compara.
     * @return {@code true} si el animal satisface todos los criterios de la preferencia;
     *         {@code false} en caso contrario.
     */
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
