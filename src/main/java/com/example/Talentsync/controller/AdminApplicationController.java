package com.example.Talentsync.controller;

import com.example.Talentsync.entity.Application;
import com.example.Talentsync.service.ApplicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/applications")
public class AdminApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // ============================
    // Admin - Get All Applications
    // ============================

    @GetMapping
    public List<Application> getAllApplications() {

        return applicationService.getAllApplications();

    }
}