package com.example.Talentsync.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ==========================
    // User Name
    // ==========================

    @NotBlank(message = "Name is required")
    private String name;


    // ==========================
    // Email
    // ==========================

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;


    // ==========================
    // Password
    // ==========================

    @NotBlank(message = "Password is required")
    @Size(
            min = 4,
            message = "Password must be at least 4 characters"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    // ==========================
    // Role
    // ==========================

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;


    // ==========================
    // Resume
    // ==========================

    @Column(name = "resume_path")
    private String resumePath;


    @Column(name = "resume_updated_at")
    private LocalDateTime resumeUpdatedAt;


    // ==========================
    // Recruiter Approval
    // ==========================

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    // ==========================
// Password Reset
// ==========================

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

}
