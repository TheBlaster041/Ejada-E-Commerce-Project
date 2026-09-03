package com.example.inventory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final Path uploadDirectory;

    public ImageStorageService(@Value("${file.upload-dir:uploads/products}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create image upload directory", e);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only JPEG, PNG, WEBP and GIF images are allowed");
        }

        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = "";

        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot).toLowerCase();
        }

        String filename = UUID.randomUUID() + extension;

        try {
            Path target = uploadDirectory.resolve(filename).normalize();

            if (!target.getParent().equals(uploadDirectory)) {
                throw new IllegalArgumentException("Invalid file path");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Store only the relative path in the database.
            return "uploads/products/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Could not store image", e);
        }
    }

    public void delete(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return;

        try {
            Path file = Paths.get(imagePath).toAbsolutePath().normalize();
            if (file.startsWith(uploadDirectory)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ignored) {
            // Image deletion should not break the main operation.
        }
    }
}
