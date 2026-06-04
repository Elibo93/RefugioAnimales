package es.refugio.refugio.domain.error;

public class AnimalYaAdoptadoException extends RuntimeException {
    public AnimalYaAdoptadoException(Integer animalId) {
        super("El animal con ID " + animalId + " ya tiene una adopción activa o en proceso.");
    }
}
