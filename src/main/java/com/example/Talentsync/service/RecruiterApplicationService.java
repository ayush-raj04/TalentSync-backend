package com.example.Talentsync.service;

import com.example.Talentsync.entity.ApprovalStatus;
import com.example.Talentsync.entity.RecruiterApplication;
import com.example.Talentsync.entity.Role;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.RecruiterApplicationRepository;
import com.example.Talentsync.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecruiterApplicationService {

    @Autowired
    private RecruiterApplicationRepository recruiterApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // =====================================================
    // Submit Recruiter Application
    // =====================================================

    public RecruiterApplication submitApplication(
            RecruiterApplication application) {

        // Check if account already exists
        if (userRepository.existsByEmail(
                application.getOfficialEmail())) {

            throw new RuntimeException(
                    "An account already exists with this email"
            );
        }


        // Check if application already exists
        if (recruiterApplicationRepository
                .existsByOfficialEmail(
                        application.getOfficialEmail())) {

            throw new RuntimeException(
                    "Recruiter application already exists"
            );
        }


        // Hash password before saving
        application.setPassword(
                passwordEncoder.encode(
                        application.getPassword()
                )
        );


        // New application is pending
        application.setStatus(
                ApprovalStatus.PENDING
        );


        // Creation time
        application.setCreatedAt(
                LocalDateTime.now()
        );


        return recruiterApplicationRepository.save(
                application
        );
    }


    // =====================================================
    // Get Pending Recruiter Applications
    // =====================================================

    public List<RecruiterApplication>
    getPendingApplications() {

        return recruiterApplicationRepository
                .findByStatus(
                        ApprovalStatus.PENDING
                );
    }


    // =====================================================
    // Approve Recruiter
    // =====================================================

    public String approveRecruiter(Long id) {

        RecruiterApplication application =
                recruiterApplicationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruiter application not found"
                                )
                        );


        // Application already processed
        if (application.getStatus()
                != ApprovalStatus.PENDING) {

            throw new RuntimeException(
                    "Application already processed"
            );
        }


        // Double check email
        if (userRepository.existsByEmail(
                application.getOfficialEmail())) {

            throw new RuntimeException(
                    "User already exists with this email"
            );
        }


        // =================================================
        // Create actual User account
        // =================================================

        User recruiter = new User();


        recruiter.setName(
                application.getRecruiterFullName()
        );


        recruiter.setEmail(
                application.getOfficialEmail()
        );


        // Password is already BCrypt encoded
        recruiter.setPassword(
                application.getPassword()
        );


        // Set recruiter role
        recruiter.setRole(
                Role.RECRUITER
        );


        // ⭐ VERY IMPORTANT
        // Recruiter is approved by Admin
        recruiter.setApprovalStatus(
                ApprovalStatus.APPROVED
        );


        // Save actual recruiter account
        userRepository.save(
                recruiter
        );


        // =================================================
        // Mark recruiter application as APPROVED
        // =================================================

        application.setStatus(
                ApprovalStatus.APPROVED
        );


        recruiterApplicationRepository.save(
                application
        );


        return "Recruiter approved successfully ✅";
    }


    // =====================================================
    // Reject Recruiter
    // =====================================================

    public String rejectRecruiter(Long id) {

        RecruiterApplication application =
                recruiterApplicationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruiter application not found"
                                )
                        );


        // Already processed
        if (application.getStatus()
                != ApprovalStatus.PENDING) {

            throw new RuntimeException(
                    "Application already processed"
            );
        }


        // Mark as rejected
        application.setStatus(
                ApprovalStatus.REJECTED
        );


        recruiterApplicationRepository.save(
                application
        );


        return "Recruiter application rejected ❌";
    }
}