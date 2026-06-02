package es.refugio.refugio.application.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con File Storage.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FileStorageService {

    private final String uploadDir = "uploads/animales";

    public FileStorageService() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de subidas", e);
        }
    }

    /**
     * Almacena un fichero de imagen generando un nombre automático sin prefijo personalizado.
     * Equivalente a llamar a {@link #storeFile(MultipartFile, String)} con {@code preferredFileName=null}.
     *
     * @param file El fichero multipart a guardar.
     * @return La URL relativa al recurso guardado (p.ej. {@code /api/v1/animales/images/animal_abc12345.jpg}),
     *         o {@code null} si el fichero es nulo o está vacío.
     */
    public String storeFile(MultipartFile file) {
        return storeFile(file, null);
    }

    /**
     * Almacena un fichero de imagen en el directorio de subidas, sanitizando el nombre
     * del fichero (eliminando tildes, espacios y caracteres especiales) y añadiendo
     * un sufijo UUID de 8 caracteres para garantizar la unicidad del nombre.
     *
     * @param file              El fichero multipart a guardar. Si es nulo o está vacío, devuelve {@code null}.
     * @param preferredFileName Nombre base deseado para el fichero (sin extensión). Si es nulo o en blanco,
     *                          se usa {@code "animal"} como nombre por defecto.
     * @return La URL relativa al recurso guardado, en formato {@code /api/v1/animales/images/[nombre_generado]}.
     * @throws RuntimeException Si ocurre un error de E/S durante la escritura del fichero.
     */
    public String storeFile(MultipartFile file, String preferredFileName) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            String extension = getFileExtension(file.getOriginalFilename());
            String sanitizedName = "animal";
            
            if (preferredFileName != null && !preferredFileName.isBlank()) {
                sanitizedName = preferredFileName.trim()
                    .toLowerCase()
                    .replace(" ", "_")
                    .replaceAll("[áàäâ]", "a")
                    .replaceAll("[éèëê]", "e")
                    .replaceAll("[íìïî]", "i")
                    .replaceAll("[óòöô]", "o")
                    .replaceAll("[úùüû]", "u")
                    .replaceAll("[ñ]", "n")
                    .replaceAll("[^a-z0-9_]", "");
            }

            String fileName = sanitizedName + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path targetPath = Paths.get(uploadDir).resolve(fileName);

            Files.copy(file.getInputStream(), targetPath);

            return "/api/v1/animales/images/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
