package com.example.Talentsync.repository;

import com.example.Talentsync.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Recruiter's jobs
    List<Job> findByRecruiterId(Long recruiterId);
    List<Job> findAllByOrderByIdDesc();

    // Recruiter's total jobs
    long countByRecruiterId(Long recruiterId);

    // Company ke saare jobs - case insensitive
    List<Job> findByCompanyIgnoreCase(String company);
}