package com.example.Talentsync.repository;

import com.example.Talentsync.entity.ApprovalStatus;
import com.example.Talentsync.entity.RecruiterApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecruiterApplicationRepository
        extends JpaRepository<RecruiterApplication, Long> {

    List<RecruiterApplication> findByStatus(
            ApprovalStatus status
    );

    Optional<RecruiterApplication> findByOfficialEmail(
            String officialEmail
    );

    boolean existsByOfficialEmail(
            String officialEmail
    );

    long countByStatus(
            ApprovalStatus status
    );
}