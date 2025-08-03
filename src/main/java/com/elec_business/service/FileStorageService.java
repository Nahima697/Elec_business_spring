package com.elec_business.service;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload.folder}")
    private String uploadFolderString;

    private Path uploadFolder;

    @PostConstruct
    public void init() {
        this.uploadFolder = Paths.get(uploadFolderString);
        try {
            Files.createDirectories(uploadFolder);
        } catch (IOException e) {
            logger.error("Unable to create upload directory: {}", uploadFolder, e);
            throw new RuntimeException("Unable to create upload directory", e);
        }
    }

    public String upload(MultipartFile file) {
        if (!checkMediaType(file, "image")) {
            throw new InvalidMediaTypeException("image", "Invalid media type uploaded");
        }

        if(file == null) {
           String filename = "default.png";
        }

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;


        try {
            createThumbnail(file, filename);
            file.transferTo(uploadFolder.resolve(filename));
        } catch (IllegalStateException | IOException e) {
            logger.error("Error transferring file to upload folder: {}", uploadFolder.resolve(filename), e);
            throw new RuntimeException("Error transferring file to upload folder " + uploadFolder, e);
        }

        return filename;
    }

    public void removeExisting(String filename) {
        Path filePath = uploadFolder.resolve(filename);
        Path thumbnailPath = uploadFolder.resolve("thumbnail-" + filename);
        try {
            Files.deleteIfExists(filePath);
            Files.deleteIfExists(thumbnailPath);
        } catch (IOException e) {
            logger.error("Error deleting uploaded file: {}", filePath, e);
            throw new RuntimeException("Error deleting uploaded file " + filePath, e);
        }
    }

    public void createThumbnail(MultipartFile file, String filename) throws IOException {
        Thumbnails.of(file.getInputStream())
                .crop(Positions.CENTER)
                .size(300, 300)
                .toFile(uploadFolder.resolve("thumbnail-" + filename).toFile());
    }

    public boolean checkMediaType(MultipartFile file, String expectedType) {
        Detector detector = new DefaultDetector();
        Metadata metadata = new Metadata();
        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream())) {
            MediaType mediaType = detector.detect(bis, metadata);
            return mediaType.getType().equals(expectedType);
        } catch (IOException e) {
            logger.error("Error detecting media type", e);
        }
        return false;
    }
}
