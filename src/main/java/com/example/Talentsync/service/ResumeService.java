package com.example.Talentsync.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResumeService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String uploadResume(MultipartFile file) throws IOException {

        File folder = new File(uploadDir);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName =
                System.currentTimeMillis() + "_" + file.getOriginalFilename();

        File destination = new File(folder, fileName);

        file.transferTo(destination);

        System.out.println(destination.getAbsolutePath());

        return fileName;
    }
    public Resource downloadResume(String fileName) throws MalformedURLException {

        Path path = Paths.get(uploadDir).resolve(fileName);

        Resource resource = new UrlResource(path.toUri());

        if (resource.exists()) {
            return resource;
        }

        throw new RuntimeException("Resume not found.");
    }
}