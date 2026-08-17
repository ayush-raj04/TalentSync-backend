package com.example.Talentsync.controller;

import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.UserRepository;
import com.example.Talentsync.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserRepository userRepository;

    // Upload Resume
    @PostMapping("/upload")
    public String uploadResume(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) throws Exception {

        User user = (User) authentication.getPrincipal();

        String fileName = resumeService.uploadResume(file);

        user.setResumePath(fileName);
        user.setResumeUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "Resume uploaded successfully.";
    }

    // View Resume
    @GetMapping("/view")
    public ResponseEntity<Resource> viewResume(
            Authentication authentication) throws Exception {

        User user = (User) authentication.getPrincipal();

        if (user.getResumePath() == null) {
            throw new RuntimeException("Resume not uploaded.");
        }

        Resource resource =
                resumeService.downloadResume(user.getResumePath());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }

    // Delete Resume
    @DeleteMapping("/delete")
    public String deleteResume(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        user.setResumePath(null);
        user.setResumeUpdatedAt(null);

        userRepository.save(user);

        return "Resume deleted successfully.";
    }

}