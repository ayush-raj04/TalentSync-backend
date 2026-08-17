package com.example.Talentsync.controller;

import com.example.Talentsync.service.PasswordResetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;


    // ============================
    // Forgot Password
    // ============================

    @PostMapping("/forgot")
    public String forgotPassword(
            @RequestParam String email) {

        String token =
                passwordResetService
                        .createResetToken(email);


        // Temporary:
        // Abhi frontend email link handle karega.

        return token;
    }


    // ============================
    // Reset Password
    // ============================

    @PostMapping("/reset")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        passwordResetService.resetPassword(
                token,
                newPassword
        );

        return "Password reset successfully ✅";
    }
}