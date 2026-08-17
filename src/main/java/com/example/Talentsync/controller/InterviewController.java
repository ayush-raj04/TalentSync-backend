package com.example.Talentsync.controller;

import com.example.Talentsync.entity.Interview;
import com.example.Talentsync.service.InterviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;


    // ============================
    // Schedule Interview
    // ============================

    @PostMapping("/schedule/{applicationId}")
    public Interview scheduleInterview(
            @PathVariable Long applicationId,
            @RequestBody Interview interview) {

        return interviewService.scheduleInterview(
                applicationId,
                interview
        );
    }


    // ============================
    // Recruiter Interviews
    // ============================

    @GetMapping("/recruiter")
    public List<Interview> getRecruiterInterviews() {

        return interviewService.getRecruiterInterviews();
    }


    // ============================
    // Candidate Interviews
    // ============================

    @GetMapping("/candidate")
    public List<Interview> getCandidateInterviews() {

        return interviewService.getCandidateInterviews();
    }


    // ============================
    // Candidate Start Interview
    // ============================

    @PutMapping("/{interviewId}/start")
    public Interview startInterview(
            @PathVariable Long interviewId) {

        return interviewService.startInterview(
                interviewId
        );
    }


    // ============================
    // Complete Interview
    // ============================

    @PutMapping("/{interviewId}/complete")
    public Interview completeInterview(
            @PathVariable Long interviewId) {

        return interviewService.completeInterview(
                interviewId
        );
    }

}