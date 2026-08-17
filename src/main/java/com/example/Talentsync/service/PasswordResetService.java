package com.example.Talentsync.service;

import com.example.Talentsync.entity.PasswordResetToken;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.PasswordResetTokenRepository;
import com.example.Talentsync.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;


    // ============================
    // Create Reset Token
    // ============================

    public String createResetToken(String email) {

        User user =
                userRepository.findByEmail(email);

        if (user == null) {

            throw new RuntimeException(
                    "No account found with this email."
            );
        }


        // Delete old token

        tokenRepository.deleteByUserId(
                user.getId()
        );


        // Generate token

        String token =
                UUID.randomUUID().toString();


        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setToken(token);

        resetToken.setUser(user);

        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );


        tokenRepository.save(resetToken);


        // ============================
        // Send Email
        // ============================

        String resetLink =
                "http://localhost:5173/reset-password?token="
                        + token;


        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(user.getEmail());

        message.setSubject(
                "TalentSync - Reset Your Password"
        );

        message.setText(
                "Hello " + user.getName() + ",\n\n"
                        + "We received a request to reset your TalentSync password.\n\n"
                        + "Click the link below to reset your password:\n\n"
                        + resetLink + "\n\n"
                        + "This link will expire in 15 minutes.\n\n"
                        + "If you did not request a password reset, you can safely ignore this email.\n\n"
                        + "Regards,\n"
                        + "TalentSync Team"
        );


        mailSender.send(message);

        System.out.println(">>> Sending reset email to: " + user.getEmail());

        mailSender.send(message);

        System.out.println(">>> EMAIL SENT SUCCESSFULLY");


        return "Password reset link has been sent to your email.";
    }


    // ============================
    // Reset Password
    // ============================

    public void resetPassword(
            String token,
            String newPassword) {

        PasswordResetToken resetToken =
                tokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid or expired reset link."
                                )
                        );


        // Check expiry

        if (resetToken
                .getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            tokenRepository.delete(resetToken);

            throw new RuntimeException(
                    "Reset link has expired."
            );
        }


        User user =
                resetToken.getUser();


        // Encrypt password

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );


        userRepository.save(user);


        // Token usable only once

        tokenRepository.delete(resetToken);
    }
}