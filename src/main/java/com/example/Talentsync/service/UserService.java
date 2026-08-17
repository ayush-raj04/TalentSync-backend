package com.example.Talentsync.service;

import com.example.Talentsync.entity.Role;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.List;
import com.example.Talentsync.entity.ApprovalStatus;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // signup logic
    public User signup(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Agar role nahi diya gaya
        // to normal signup = CANDIDATE
        if (user.getRole() == null) {

            user.setRole(Role.CANDIDATE);

        }

        // Candidate directly approved
        if (user.getRole() == Role.CANDIDATE) {

            user.setApprovalStatus(
                    ApprovalStatus.APPROVED
            );

        }

        // Recruiter ko admin approve karega
        else if (user.getRole() == Role.RECRUITER) {

            user.setApprovalStatus(
                    ApprovalStatus.PENDING
            );

        }

        // Admin ke liye bhi approved
        else if (user.getRole() == Role.ADMIN) {

            user.setApprovalStatus(
                    ApprovalStatus.APPROVED
            );

        }

        return userRepository.save(user);
    }

    // login logic
    public String login(User user) {

        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser == null) {
            return "User not found ❌";
        }

        if (!existingUser.getPassword().equals(user.getPassword())) {
            return "Wrong password ❌";
        }

        return "Login successful ✅";
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Admin can't be deleted
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException(
                    "Admin account cannot be deleted."
            );
        }

        userRepository.delete(user);
    }

    // ============================
// Forgot Password
// ============================

    public String createResetToken(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("No account found with this email.");
        }

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);

        user.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(15)
        );

        userRepository.save(user);

        return token;
    }


// ============================
// Find User By Reset Token
// ============================

    public User findByResetToken(String token) {

        return userRepository.findByResetToken(token);
    }


// ============================
// Reset Password
// ============================

    public void resetPassword(
            String token,
            String newPassword) {

        User user =
                userRepository.findByResetToken(token);

        if (user == null) {

            throw new RuntimeException(
                    "Invalid reset link."
            );
        }

        if (
                user.getResetTokenExpiry() == null ||
                        user.getResetTokenExpiry()
                                .isBefore(LocalDateTime.now())
        ) {

            throw new RuntimeException(
                    "Reset link has expired."
            );
        }

        user.setPassword(
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode(newPassword)
        );

        user.setResetToken(null);

        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }
}