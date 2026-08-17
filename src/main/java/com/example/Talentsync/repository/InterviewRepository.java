package com.example.Talentsync.repository;

import com.example.Talentsync.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository
        extends JpaRepository<Interview, Long> {

    // Recruiter's scheduled interviews
    List<Interview> findByApplicationJobRecruiterId(Long recruiterId);

    // Candidate's scheduled interviews
    List<Interview> findByApplicationCandidateId(Long candidateId);

    // Interviews for a particular application
    List<Interview> findByApplicationId(Long applicationId);
}