package com.example.Talentsync.controller;

import com.example.Talentsync.dto.DashboardResponse;
import com.example.Talentsync.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Talentsync.dto.RecruiterDashboardResponse;

@RestController
public class DashboardController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/api/dashboard/candidate")
    public DashboardResponse getCandidateDashboard() {

        return applicationService.getCandidateDashboard();


    }
    @GetMapping("/api/dashboard/recruiter")
    public RecruiterDashboardResponse getRecruiterDashboard() {

        return applicationService.getRecruiterDashboard();

    }
    @GetMapping("/hello")
    public String hello() {
        return "Dashboard Controller Working";
    }
}