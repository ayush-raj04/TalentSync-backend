package com.example.Talentsync.service;

import com.example.Talentsync.entity.Application;
import com.example.Talentsync.entity.ApplicationStatus;
import com.example.Talentsync.entity.Interview;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.ApplicationRepository;
import com.example.Talentsync.repository.InterviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NotificationService notificationService;


    // ============================
    // Schedule Interview
    // ============================

    public Interview scheduleInterview(
            Long applicationId,
            Interview interview) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User recruiter =
                (User) authentication.getPrincipal();


        // Find application

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found."
                                )
                        );


        // Security Check

        if (!application.getJob()
                .getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "Access Denied! This is not your job."
            );
        }


        // Only shortlisted candidates

        if (application.getStatus()
                != ApplicationStatus.SHORTLISTED) {

            throw new RuntimeException(
                    "Interview can only be scheduled for shortlisted candidates."
            );
        }


        // Connect interview with application

        interview.setApplication(application);


        // Interview scheduled

        interview.setStatus("SCHEDULED");


        // Application status

        application.setStatus(
                ApplicationStatus.INTERVIEW
        );

        applicationRepository.save(application);


        // Save Interview

        Interview savedInterview =
                interviewRepository.save(interview);


        // Candidate notification

        notificationService.createNotification(

                application.getCandidate(),

                "📅 Interview Scheduled",

                "There's an interview with "
                        + application.getJob().getCompany()
                        + ". Click to see more details.",

                "INTERVIEW",

                savedInterview.getId()
        );


        return savedInterview;
    }


    // ============================
    // Recruiter Interviews
    // ============================

    public List<Interview> getRecruiterInterviews() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User recruiter =
                (User) authentication.getPrincipal();


        return interviewRepository
                .findByApplicationJobRecruiterId(
                        recruiter.getId()
                );
    }


    // ============================
    // Candidate Interviews
    // ============================

    public List<Interview> getCandidateInterviews() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User candidate =
                (User) authentication.getPrincipal();


        return interviewRepository
                .findByApplicationCandidateId(
                        candidate.getId()
                );
    }


    // ============================
    // Candidate Start Interview
    // ============================

    public Interview startInterview(
            Long interviewId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User candidate =
                (User) authentication.getPrincipal();


        // Find interview

        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Interview not found."
                                )
                        );


        // ============================
        // Security Check
        // ============================

        if (!interview
                .getApplication()
                .getCandidate()
                .getId()
                .equals(candidate.getId())) {

            throw new RuntimeException(
                    "Access Denied! This is not your interview."
            );
        }


        // ============================
        // Already Attempted
        // ============================

        if ("COMPLETED".equals(
                interview.getStatus())) {

            throw new RuntimeException(
                    "You have already attempted this interview."
            );
        }


        // ============================
        // Interview Date & Time
        // ============================

        LocalDate interviewDate =
                interview.getInterviewDate();

        LocalTime interviewTime =
                interview.getInterviewTime();


        LocalDateTime scheduledDateTime =
                LocalDateTime.of(
                        interviewDate,
                        interviewTime
                );


        LocalDateTime currentDateTime =
                LocalDateTime.now();


        // ============================
        // Opening Time
        // 15 minutes before
        // ============================

        LocalDateTime openingDateTime =
                scheduledDateTime.minusMinutes(15);


        // ============================
        // Too Early
        // ============================

        if (currentDateTime.isBefore(
                openingDateTime)) {

            throw new RuntimeException(
                    "Interview meeting is not open yet. "
                            + "You can join 15 minutes before the scheduled time."
            );
        }


        // ============================
        // Too Late
        // ============================

        if (currentDateTime.isAfter(
                scheduledDateTime)) {

            throw new RuntimeException(
                    "Sorry, you can't open this meeting "
                            + "because the scheduled interview "
                            + "date/time has passed."
            );
        }


        // ============================
        // One Attempt
        // ============================

        interview.setStatus("COMPLETED");


        return interviewRepository.save(
                interview
        );
    }


    // ============================
    // Complete Interview
    // ============================

    public Interview completeInterview(
            Long interviewId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User recruiter =
                (User) authentication.getPrincipal();


        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Interview not found."
                                )
                        );


        // Security Check

        if (!interview
                .getApplication()
                .getJob()
                .getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "Access Denied! This is not your interview."
            );
        }


        // Update status

        interview.setStatus("COMPLETED");


        return interviewRepository.save(
                interview
        );
    }
    // ============================
// Admin - Get All Interviews
// ============================

    public List<Interview> getAllInterviews() {

        return interviewRepository.findAll();

    }

}