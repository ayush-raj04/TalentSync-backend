package com.example.Talentsync.controller;

import com.example.Talentsync.entity.RecruiterApplication;
import com.example.Talentsync.entity.Role;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.security.JwtUtil;
import com.example.Talentsync.service.RecruiterApplicationService;
import com.example.Talentsync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecruiterApplicationController {

    @Autowired
    private RecruiterApplicationService recruiterApplicationService;

    @Autowired
    private UserService userService;


    // ==========================
    // Recruiter Signup
    // ==========================

    @PostMapping("/recruiter/signup")
    public RecruiterApplication signup(
            @RequestBody RecruiterApplication application) {

        return recruiterApplicationService
                .submitApplication(application);
    }


    // ==========================
    // Admin - Pending Recruiters
    // ==========================

    @GetMapping("/admin/recruiter-applications")
    public Object getPendingApplications(
            @RequestHeader("Authorization") String token) {

        try {

            String actualToken =
                    token.replace("Bearer ", "");

            String email =
                    JwtUtil.extractEmail(actualToken);

            User admin =
                    userService.findByEmail(email);

            if (admin == null) {
                return "Invalid user ❌";
            }

            if (admin.getRole() != Role.ADMIN) {
                return "Access Denied ❌ (Only ADMIN allowed)";
            }

            return recruiterApplicationService
                    .getPendingApplications();

        } catch (Exception e) {

            return "Invalid Token ❌";
        }
    }


    // ==========================
    // Admin - Approve
    // ==========================

    @PutMapping("/admin/recruiter-applications/{id}/approve")
    public Object approveRecruiter(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        try {

            String actualToken =
                    token.replace("Bearer ", "");

            String email =
                    JwtUtil.extractEmail(actualToken);

            User admin =
                    userService.findByEmail(email);

            if (admin == null) {
                return "Invalid user ❌";
            }

            if (admin.getRole() != Role.ADMIN) {
                return "Access Denied ❌ (Only ADMIN allowed)";
            }

            return recruiterApplicationService
                    .approveRecruiter(id);

        } catch (Exception e) {

            return e.getMessage();
        }
    }


    // ==========================
    // Admin - Reject
    // ==========================

    @PutMapping("/admin/recruiter-applications/{id}/reject")
    public Object rejectRecruiter(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        try {

            String actualToken =
                    token.replace("Bearer ", "");

            String email =
                    JwtUtil.extractEmail(actualToken);

            User admin =
                    userService.findByEmail(email);

            if (admin == null) {
                return "Invalid user ❌";
            }

            if (admin.getRole() != Role.ADMIN) {
                return "Access Denied ❌ (Only ADMIN allowed)";
            }

            return recruiterApplicationService
                    .rejectRecruiter(id);

        } catch (Exception e) {

            return e.getMessage();
        }
    }
}