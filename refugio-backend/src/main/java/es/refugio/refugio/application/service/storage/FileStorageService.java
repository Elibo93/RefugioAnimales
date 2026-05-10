package es.refugio.refugio.application.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = "uploads/animales";

    public FileStorageService() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de subidas", e);
        }
    }

    public String storeFile(MultipartFile file) {
        return storeFile(file, null);
    }

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
