package com.example.Talentsync.repository;

import com.example.Talentsync.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Talentsync.entity.ApplicationStatus;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByCandidateId(Long candidateId);

    List<Application> findByJobId(Long jobId);
    Optional<Application> findByCandidateIdAndJobId(Long candidateId, Long jobId);

    long countByCandidateId(Long candidateId);

    long countByCandidateIdAndStatus(Long candidateId, ApplicationStatus status);

    long countByJobRecruiterId(Long recruiterId);

    List<Application> findTop5ByJobRecruiterIdOrderByAppliedDateDesc(Long recruiterId);

}