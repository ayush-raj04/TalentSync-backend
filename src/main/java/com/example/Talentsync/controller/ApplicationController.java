package com.example.Talentsync.controller;

import com.example.Talentsync.dto.ApplicationRequest;
import com.example.Talentsync.entity.Application;
import com.example.Talentsync.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // Apply Job
    @PostMapping("/apply")
    public Application applyJob(@Valid @RequestBody ApplicationRequest request) {
        return applicationService.applyJob(request);
    }

    // Logged-in Candidate Applications
    @GetMapping("/my")
    public List<Application> getMyApplications() {
        return applicationService.getMyApplications();
    }

    // Recruiter View Applicants
    @GetMapping("/job/{jobId}")
    public List<Application> getApplicants(@PathVariable Long jobId) {
        return applicationService.getApplicants(jobId);
    }

    // Update Status
    @PutMapping("/{applicationId}/status")
    public Application updateStatus(
            @PathVariable Long applicationId,
            @RequestBody Application application) {

        return applicationService.updateStatus(
                applicationId,
                application.getStatus()
        );
    }

    // Upload Resume
    @PostMapping("/{applicationId}/uploadResume")
    public Application uploadResume(
            @PathVariable Long applicationId,
            @RequestParam("file") MultipartFile file) throws Exception {

        return applicationService.uploadResume(applicationId, file);
    }

    // Download Resume
    @GetMapping("/{applicationId}/resume")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long applicationId) throws Exception {

        Resource resource =
                applicationService.downloadResumeByApplicationId(applicationId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }
    // Withdraw Application
    @DeleteMapping("/{applicationId}/withdraw")
    public String withdrawApplication(
            @PathVariable Long applicationId) {

        applicationService.withdrawApplication(applicationId);

        return "Application withdrawn successfully.";
    }
    // ============================
// Reject Application
// ============================

    @PutMapping("/{applicationId}/reject")
    public Application rejectApplication(
            @PathVariable Long applicationId) {

        return applicationService.rejectApplication(
                applicationId
        );
    }
    // ============================
// Select Candidate / Offer Letter
// ============================

    @PutMapping("/{applicationId}/select")
    public Application selectApplication(
            @PathVariable Long applicationId) {

        return applicationService.selectApplication(
                applicationId
        );
    }
}