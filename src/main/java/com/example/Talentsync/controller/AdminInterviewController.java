package com.example.Talentsync.controller;

import com.example.Talentsync.entity.Interview;
import com.example.Talentsync.service.InterviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/interviews")
public class AdminInterviewController {

    @Autowired
    private InterviewService interviewService;

    // ============================
    // Admin - Get All Interviews
    // ============================

    @GetMapping
    public List<Interview> getAllInterviews() {

        return interviewService.getAllInterviews();

    }
}