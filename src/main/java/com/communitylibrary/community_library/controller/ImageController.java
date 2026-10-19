
package com.communitylibrary.community_library.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:5173")
public class ImageController {

    private static final Path UPLOAD_DIR =
            Paths.get("uploads").toAbsolutePath().normalize();

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Please select an image"));
            }

            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Only JPG, PNG and WEBP images are allowed"));
            }

            Files.createDirectories(UPLOAD_DIR);

            String contentType = file.getContentType();
            String extension = switch (contentType) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> throw new IllegalArgumentException("Unsupported image type");
            };

            String filename = UUID.randomUUID() + extension;
            Path destination = UPLOAD_DIR.resolve(filename).normalize();

            if (!destination.startsWith(UPLOAD_DIR)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid filename"));
            }

            Files.copy(file.getInputStream(), destination);

            return ResponseEntity.ok(Map.of(
                    "imageUrl", "/uploads/" + filename
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Could not save image"));
        }
    }
}
