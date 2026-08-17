package com.example.Talentsync.controller;

import com.example.Talentsync.dto.JobRequest;
import com.example.Talentsync.entity.Job;
import com.example.Talentsync.service.JobService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;


    // ============================
    // Recruiter - Post Job
    // ============================

    @PostMapping
    public Job addJob(
            @Valid @RequestBody JobRequest request) {

        return jobService.addJob(request);
    }


    // ============================
    // All Jobs
    // ============================

    @GetMapping
    public List<Job> getAllJobs() {

        return jobService.getAllJobs();
    }


    // ============================
    // Recruiter - My Jobs
    // ============================

    @GetMapping("/my")
    public List<Job> getMyJobs() {

        return jobService.getMyJobs();
    }


    // ============================
    // Job By Id
    // ============================

    @GetMapping("/{id}")
    public Job getJobById(
            @PathVariable Long id) {

        return jobService.getJobById(id);
    }


    // ============================
    // Delete Job
    // ============================

    @DeleteMapping("/{id}")
    public String deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return "Job Deleted Successfully ✅";
    }


    // ============================
    // ADMIN - Company List
    // ============================

    @GetMapping("/admin/companies")
    public List<String> getCompanies() {

        return jobService.getCompanies();
    }


    // ============================
    // ADMIN - Company Jobs
    // ============================

    @GetMapping("/admin/company")
    public List<Job> getJobsByCompany(
            @RequestParam String company) {

        return jobService.getJobsByCompany(company);
    }
}