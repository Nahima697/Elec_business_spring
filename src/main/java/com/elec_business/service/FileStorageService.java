package com.elec_business.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final Cloudinary cloudinary;
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public String upload(MultipartFile file) {
        // 1. Validation basique
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Impossible d'uploader un fichier vide");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new InvalidMediaTypeException("image", "Le fichier doit être une image");
        }

        try {
            // 2. Upload vers Cloudinary
            String publicId = UUID.randomUUID().toString();

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "elec_business" // Optionnel : crée un dossier dans Cloudinary
            ));

            // 3. On récupère l'URL sécurisée (https) générée par Cloudinary
            String url = (String) uploadResult.get("secure_url");

            logger.info("Image uploadée avec succès sur Cloudinary : {}", url);
            return url;

        } catch (IOException e) {
            logger.error("Erreur lors de l'upload Cloudinary", e);
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
    }

    // Plus besoin de checkMediaType compliqué, on fait confiance au contentType du MultipartFile pour ce niveau
    public boolean checkMediaType(MultipartFile file, String expectedType) {
        return file.getContentType() != null && file.getContentType().startsWith(expectedType);
    }

    // Cloudinary gère la suppression
    public void removeExisting(String imageUrl) {
    }
}