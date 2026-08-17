package com.example.Talentsync.service;

import com.example.Talentsync.dto.AdminDashboardResponse;
import com.example.Talentsync.entity.ApprovalStatus;
import com.example.Talentsync.entity.Role;
import com.example.Talentsync.repository.RecruiterApplicationRepository;
import com.example.Talentsync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecruiterApplicationRepository recruiterApplicationRepository;


    // ============================
    // Admin Dashboard Statistics
    // ============================

    public AdminDashboardResponse getDashboardStats() {

        long totalUsers =
                userRepository.count();

        long candidates =
                userRepository.countByRole(
                        Role.CANDIDATE
                );

        long recruiters =
                userRepository.countByRole(
                        Role.RECRUITER
                );

        long pendingApprovals =
                recruiterApplicationRepository.countByStatus(
                        ApprovalStatus.PENDING
                );

        return new AdminDashboardResponse(
                totalUsers,
                candidates,
                recruiters,
                pendingApprovals
        );
    }
}