package com.gaia.service.impl;

import com.gaia.dto.upload.ImageUploadResponse;
import com.gaia.exception.ApiException;
import com.gaia.service.FileStorageService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_BYTES = 5 * 1024 * 1024;

    private final Path uploadPath;
    private final String publicBaseUrl;

    public FileStorageServiceImpl(
            @Value("${app.upload.dir}") String uploadDir,
            @Value("${app.public-base-url}") String publicBaseUrl
    ) {
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl.replaceAll("/$", "");
    }

    @Override
    public ImageUploadResponse storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Debe seleccionar una imagen", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_BYTES) {
            throw new ApiException("La imagen no puede superar 5MB", HttpStatus.BAD_REQUEST);
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "imagen" : file.getOriginalFilename());
        String extension = extractExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ApiException("Formato de imagen no permitido", HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ApiException("Formato de imagen no permitido", HttpStatus.BAD_REQUEST);
        }
        validateImageSignature(file, extension);

        try {
            Files.createDirectories(uploadPath);
            String safeName = sanitize(originalName.replaceAll("\\.[^.]+$", ""));
            String fileName = safeName + "-" + UUID.randomUUID() + "." + extension;
            Path target = uploadPath.resolve(fileName).normalize();
            if (!target.startsWith(uploadPath)) {
                throw new ApiException("Nombre de archivo inválido", HttpStatus.BAD_REQUEST);
            }
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return new ImageUploadResponse(fileName, publicBaseUrl + "/uploads/" + fileName);
        } catch (IOException exception) {
            throw new ApiException("No fue posible guardar la imagen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateImageSignature(MultipartFile file, String extension) {
        byte[] header = new byte[12];
        try (InputStream inputStream = file.getInputStream()) {
            int readBytes = inputStream.read(header);
            boolean valid = switch (extension) {
                case "jpg", "jpeg" -> readBytes >= 3
                        && (header[0] & 0xFF) == 0xFF
                        && (header[1] & 0xFF) == 0xD8
                        && (header[2] & 0xFF) == 0xFF;
                case "png" -> readBytes >= 8
                        && (header[0] & 0xFF) == 0x89
                        && header[1] == 0x50
                        && header[2] == 0x4E
                        && header[3] == 0x47
                        && header[4] == 0x0D
                        && header[5] == 0x0A
                        && header[6] == 0x1A
                        && header[7] == 0x0A;
                case "webp" -> readBytes >= 12
                        && header[0] == 0x52
                        && header[1] == 0x49
                        && header[2] == 0x46
                        && header[3] == 0x46
                        && header[8] == 0x57
                        && header[9] == 0x45
                        && header[10] == 0x42
                        && header[11] == 0x50;
                default -> false;
            };
            if (!valid) {
                throw new ApiException("El archivo no corresponde a una imagen válida", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException exception) {
            throw new ApiException("No fue posible leer la imagen", HttpStatus.BAD_REQUEST);
        }
    }

    private String extractExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
    }

    private String sanitize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]+", "-")
                .replaceAll("(^-|-$)", "")
                .toLowerCase(Locale.ROOT);
        return normalized.isBlank() ? "imagen" : normalized;
    }
}
