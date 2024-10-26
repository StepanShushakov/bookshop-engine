package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.model.book.BookFileEntity;
import com.example.mybookshopapp.data.repositories.BookFileRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * @author karl
 */

@Service
public class ResourceStorage {

    @Value("${upload.path}")
    String uploadPath;
    @Value("${download.path}")
    String downloadPath;
    private final BookFileRepository bookFileRepository;

    @Autowired
    public ResourceStorage(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

    public String saveNewImage(MultipartFile file, String slug) {

        String resourceURI = null;
        try {
            if(!file.isEmpty()) {
                if(!new File(uploadPath).exists()) {
                    Files.createDirectories(Paths.get(uploadPath));
                    Logger.getLogger(this.getClass().getSimpleName()).info("create image folder in " + uploadPath);
                }

                String fileName = slug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
                Path path = Paths.get(uploadPath, fileName);
                resourceURI = "/book-covers/" + fileName;
                file.transferTo(path);  //uploading user file here
                Logger.getLogger(this.getClass().getName()).info(fileName + " uploaded OK!");
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).info(e.toString());
        }

        return resourceURI;
    }

    public Path getBookFilePath(String hash) {
        BookFileEntity bookFileEntity = bookFileRepository.findBookFileEntitiesByHash(hash);
        return Paths.get(bookFileEntity.getPath());
    }

    public MediaType getBookFileMime(String hash) {
        BookFileEntity bookFileEntity = bookFileRepository.findBookFileEntitiesByHash(hash);
        String mimeType =
                URLConnection.guessContentTypeFromName(Paths.get(bookFileEntity.getPath()).getFileName().toString());
        if (mimeType != null) {
            return MediaType.parseMediaType(mimeType);
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public MediaType getBookFileMime(Path path) {
        String mimeType =
                URLConnection.guessContentTypeFromName(path.getFileName().toString());
        if (mimeType != null) {
            return MediaType.parseMediaType(mimeType);
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public byte[] getBookFileByteArray(String hash) throws IOException{
        BookFileEntity bookFileEntity = bookFileRepository.findBookFileEntitiesByHash(hash);
        Path path = Paths.get(downloadPath, bookFileEntity.getPath());
        return Files.readAllBytes(path);
    }

    public byte[] getBookFileByteArray(Path path) throws IOException {
        return Files.readAllBytes(Paths.get(downloadPath, path.toString()));
    }
}
