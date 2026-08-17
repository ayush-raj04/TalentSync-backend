package com.example.Talentsync.service;

import com.example.Talentsync.dto.ApplicationRequest;
import com.example.Talentsync.entity.Application;
import com.example.Talentsync.entity.ApplicationStatus;
import com.example.Talentsync.entity.Job;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.ApplicationRepository;
import com.example.Talentsync.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.Talentsync.dto.DashboardResponse;
import com.example.Talentsync.repository.UserRepository;
import com.example.Talentsync.dto.RecruiterDashboardResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserRepository userRepository;

    // ============================
    // Candidate Apply Job
    // ============================
    public Application applyJob(ApplicationRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User candidate = (User) authentication.getPrincipal();

        Long candidateId = candidate.getId();
        Long jobId = request.getJobId();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found."));

        Optional<Application> existingApplication =
                applicationRepository.findByCandidateIdAndJobId(candidateId, jobId);

        if (existingApplication.isPresent()) {
            throw new RuntimeException("You have already applied for this job.");
        }

        Application application = new Application();

        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedDate(LocalDate.now());

        return applicationRepository.save(application);
    }

    // ============================
    // Logged-in Candidate Applications
    // ============================
    public List<Application> getMyApplications() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User candidate = (User) authentication.getPrincipal();

        return applicationRepository.findByCandidateId(candidate.getId());
    }

    // ============================
    // Recruiter View Applicants
    // ============================
    public List<Application> getApplicants(Long jobId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User recruiter = (User) authentication.getPrincipal();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found."));

        // Security Check
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("Access Denied! This is not your job.");
        }

        return applicationRepository.findByJobId(jobId);
    }

    // ============================
    // Update Application Status
    // ============================
    public Application updateStatus(Long applicationId,
                                    ApplicationStatus status) {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException("Application not found."));

        application.setStatus(status);

        return applicationRepository.save(application);
    }
    // ============================
// Upload Resume
// ============================

    public Application uploadResume(Long applicationId,
                                    MultipartFile file) throws Exception {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException("Application not found."));

        String fileName = resumeService.uploadResume(file);

        application.setResumePath(fileName);

        return applicationRepository.save(application);

    }



    // ============================
    // Download Resume
    // ============================
    public Resource downloadResumeByApplicationId(Long applicationId)
            throws Exception {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException("Application not found."));

        String resumePath = application.getResumePath();

        // If application does not have a separate resume,
        // use candidate's current resume
        if (resumePath == null || resumePath.isEmpty()) {

            if (application.getCandidate() == null ||
                    application.getCandidate().getResumePath() == null ||
                    application.getCandidate().getResumePath().isEmpty()) {

                throw new RuntimeException("Resume not uploaded.");
            }

            resumePath = application.getCandidate().getResumePath();
        }

        return resumeService.downloadResume(resumePath);
    }
    public DashboardResponse getCandidateDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);

        User candidate = (User) authentication.getPrincipal();

        long applications =
                applicationRepository.countByCandidateId(candidate.getId());

        long pending =
                applicationRepository.countByCandidateIdAndStatus(
                        candidate.getId(),
                        ApplicationStatus.APPLIED
                );

        long shortlisted =
                applicationRepository.countByCandidateIdAndStatus(
                        candidate.getId(),
                        ApplicationStatus.SHORTLISTED
                );

        long rejected =
                applicationRepository.countByCandidateIdAndStatus(
                        candidate.getId(),
                        ApplicationStatus.REJECTED
                );

        return new DashboardResponse(
                applications,
                pending,
                shortlisted,
                rejected
        );
    }
    // ============================
// Recruiter Dashboard
// ============================

    public RecruiterDashboardResponse getRecruiterDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User recruiter = (User) authentication.getPrincipal();

        long jobsPosted =
                jobRepository.countByRecruiterId(recruiter.getId());

        long applicants =
                applicationRepository.countByJobRecruiterId(recruiter.getId());

        long activeJobs =
                jobRepository.countByRecruiterId(recruiter.getId());

        return new RecruiterDashboardResponse(

                jobsPosted,
                applicants,
                activeJobs

        );

    }
    // ============================
// Withdraw Application
// ============================
    public void withdrawApplication(Long applicationId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User candidate = (User) authentication.getPrincipal();

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException("Application not found."));

        // Security Check
        if (!application.getCandidate().getId().equals(candidate.getId())) {
            throw new RuntimeException(
                    "Access Denied! This is not your application."
            );
        }

        // Only Applied applications can be withdrawn
        if (application.getStatus() != ApplicationStatus.APPLIED) {
            throw new RuntimeException(
                    "You can only withdraw an active application."
            );
        }

        applicationRepository.delete(application);
    }
    // ============================
// Reject Application
// ============================

    public Application rejectApplication(Long applicationId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User recruiter =
                (User) authentication.getPrincipal();

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found."
                                )
                        );

        // Security Check
        if (!application.getJob()
                .getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "Access Denied! This is not your application."
            );
        }

        application.setStatus(ApplicationStatus.REJECTED);

        return applicationRepository.save(application);
    }
    // ============================
// Select Candidate
// ============================

    public Application selectApplication(Long applicationId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User recruiter =
                (User) authentication.getPrincipal();

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found."
                                )
                        );

        // Security Check
        if (!application.getJob()
                .getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "Access Denied! This is not your application."
            );
        }

        application.setStatus(ApplicationStatus.SELECTED);

        return applicationRepository.save(application);
    }
    // ============================
// Admin - Get All Applications
// ============================

    public List<Application> getAllApplications() {

        return applicationRepository.findAll();

    }
}