package com.example.Talentsync.controller;

import com.example.Talentsync.dto.LoginRequest;
import com.example.Talentsync.dto.SignupRequest;
import com.example.Talentsync.entity.ApprovalStatus;
import com.example.Talentsync.entity.Role;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.security.JwtUtil;
import com.example.Talentsync.service.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;


    // ============================
    // Home API
    // ============================

    @GetMapping("/")
    public String home() {

        return "Backend is running 🔥";
    }


    // ============================
    // Signup API
    // ============================

    @PostMapping("/api/signup")
    public User signup(
            @Valid @RequestBody SignupRequest request) {

        User user = new User();

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );


        // ============================
        // Set Role
        // ============================

        if (request.getRole() == null) {

            user.setRole(
                    Role.CANDIDATE
            );

        } else {

            user.setRole(
                    request.getRole()
            );

        }


        // Approval status
        // UserService me automatically set hoga

        return userService.signup(user);
    }


    // ============================
    // Login API
    // ============================

    @PostMapping("/api/login")
    public String login(
            @Valid @RequestBody LoginRequest request) {

        User existingUser =
                userService.findByEmail(
                        request.getEmail()
                );


        // ============================
        // User not found
        // ============================

        if (existingUser == null) {

            throw new RuntimeException(
                    "User not found"
            );

        }


        // ============================
        // Password Check
        // ============================

        if (!passwordEncoder.matches(
                request.getPassword(),
                existingUser.getPassword())) {

            throw new RuntimeException(
                    "Wrong password"
            );

        }


        // ============================
        // Recruiter Approval Check
        // ============================

        /*
         * New recruiter:
         *
         * PENDING  -> Login blocked
         * REJECTED -> Login blocked
         * APPROVED -> Login allowed
         *
         * Old recruiters:
         *
         * approvalStatus = null
         * -> Login allowed
         *
         * This keeps old recruiter accounts
         * working without affecting the
         * new approval system.
         */

        if (existingUser.getRole() == Role.RECRUITER) {

            ApprovalStatus approvalStatus =
                    existingUser.getApprovalStatus();


            // ============================
            // Rejected Recruiter
            // ============================

            if (approvalStatus == ApprovalStatus.REJECTED) {

                throw new RuntimeException(
                        "Your recruiter application has been rejected by admin."
                );

            }


            // ============================
            // Pending Recruiter
            // ============================

            if (approvalStatus == ApprovalStatus.PENDING) {

                throw new RuntimeException(
                        "Your recruiter account is waiting for admin approval."
                );

            }


            /*
             * APPROVED -> continue
             *
             * null -> old recruiter account
             * continue
             */
        }


        // ============================
        // Generate JWT
        // ============================

        return JwtUtil.generateToken(
                existingUser.getEmail(),
                existingUser.getRole().name()
        );
    }


    // ============================
    // Admin - Get All Users
    // ============================

    @GetMapping("/api/admin/users")
    public Object getAllUsers(
            @RequestHeader("Authorization")
            String token) {

        try {

            String actualToken =
                    token.replace(
                            "Bearer ",
                            ""
                    );

            String email =
                    JwtUtil.extractEmail(
                            actualToken
                    );

            User user =
                    userService.findByEmail(
                            email
                    );


            if (user == null) {

                return "Invalid user ❌";

            }


            if (user.getRole()
                    != Role.ADMIN) {

                return "Access Denied ❌ (Only ADMIN allowed)";

            }


            return userService.getAllUsers();

        } catch (Exception e) {

            return "Invalid Token ❌";
        }
    }


    // ============================
    // Admin - Delete User
    // ============================

    @DeleteMapping(
            "/api/admin/users/{userId}"
    )
    public Object deleteUser(
            @RequestHeader("Authorization")
            String token,

            @PathVariable Long userId) {

        try {

            String actualToken =
                    token.replace(
                            "Bearer ",
                            ""
                    );


            String email =
                    JwtUtil.extractEmail(
                            actualToken
                    );


            User admin =
                    userService.findByEmail(
                            email
                    );


            if (admin == null) {

                return "Invalid user ❌";

            }


            // ============================
            // Only ADMIN can delete
            // ============================

            if (admin.getRole()
                    != Role.ADMIN) {

                return "Access Denied ❌ (Only ADMIN allowed)";

            }


            userService.deleteUser(
                    userId
            );


            return "User deleted successfully ✅";


        } catch (Exception e) {

            return e.getMessage();

        }
    }

    // ============================
// Forgot Password
// ============================

    @PostMapping("/api/forgot-password")
    public String forgotPassword(
            @RequestParam String email) {

        try {

            String token =
                    userService.createResetToken(email);

            String resetLink =
                    "http://localhost:5173/reset-password/"
                            + token;

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(email);

            message.setSubject(
                    "TalentSync - Reset Your Password"
            );

            message.setText(
                    "Hello,\n\n" +
                            "We received a request to reset your TalentSync password.\n\n" +
                            "Click the link below to reset your password:\n\n" +
                            resetLink +
                            "\n\n" +
                            "This link will expire in 15 minutes.\n\n" +
                            "If you did not request this, please ignore this email.\n\n" +
                            "Regards,\n" +
                            "TalentSync Team"
            );

            mailSender.send(message);

            return "Password reset link has been sent to your email.";

        } catch (Exception e) {

            throw new RuntimeException(
                    e.getMessage()
            );
        }
    }


// ============================
// Reset Password
// ============================

    @PostMapping("/api/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String password) {

        userService.resetPassword(
                token,
                password
        );

        return "Password reset successfully.";
    }

}