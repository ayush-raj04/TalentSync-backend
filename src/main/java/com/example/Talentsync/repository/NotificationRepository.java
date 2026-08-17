package com.example.Talentsync.repository;

import com.example.Talentsync.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Candidate ki saari notifications
    List<Notification> findByCandidateIdOrderByCreatedAtDesc(
            Long candidateId
    );


    // Candidate ki unread notifications
    List<Notification>
    findByCandidateIdAndReadStatusFalseOrderByCreatedAtDesc(
            Long candidateId
    );


    // Check whether notification already exists
    Optional<Notification>
    findByCandidateIdAndTypeAndReferenceId(
            Long candidateId,
            String type,
            Long referenceId
    );
}