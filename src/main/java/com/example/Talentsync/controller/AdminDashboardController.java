package com.example.Talentsync.controller;

import com.example.Talentsync.dto.AdminDashboardResponse;
import com.example.Talentsync.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;


    // ============================
    // Admin Dashboard
    // ============================

    @GetMapping("/api/dashboard/admin")
    public AdminDashboardResponse getAdminDashboard() {

        return adminDashboardService
                .getDashboardStats();
    }
}