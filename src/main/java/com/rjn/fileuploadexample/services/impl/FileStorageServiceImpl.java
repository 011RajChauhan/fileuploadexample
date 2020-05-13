package com.rjn.fileuploadexample.services.impl;

import com.rjn.fileuploadexample.exception.FileStorageException;
import com.rjn.fileuploadexample.exception.MyFileNotFoundException;
import com.rjn.fileuploadexample.property.FileStorageProperties;
import com.rjn.fileuploadexample.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        logger.info("path retrieved ---->,{}", fileStorageLocation);
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("could not create the directory where uploaded file will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry ! file name contains invalid path sequence " + fileName);
            }
            // copy file to the target location (replace existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store " + fileName + " please try again.", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("file not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("file not found " + fileName, ex);
        }
    }
}
