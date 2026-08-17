package com.example.Talentsync.service;

import com.example.Talentsync.entity.Notification;
import com.example.Talentsync.entity.OfferLetter;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.NotificationRepository;
import com.example.Talentsync.repository.OfferLetterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OfferLetterRepository offerLetterRepository;


    // ============================
    // Create Notification
    // ============================

    public Notification createNotification(
            User candidate,
            String title,
            String message,
            String type,
            Long referenceId) {

        Notification notification =
                new Notification();

        notification.setCandidate(candidate);

        notification.setTitle(title);

        notification.setMessage(message);

        notification.setType(type);

        notification.setReferenceId(referenceId);

        notification.setReadStatus(false);

        notification.setCreatedAt(
                LocalDateTime.now()
        );

        return notificationRepository.save(
                notification
        );
    }


    // ============================
    // Sync Existing Offer Letters
    // ============================

    private void syncExistingOfferNotifications(
            User candidate) {

        List<OfferLetter> offerLetters =
                offerLetterRepository
                        .findAllByApplicationCandidateId(
                                candidate.getId()
                        );


        for (OfferLetter offerLetter : offerLetters) {

            // Only SENT offers
            if (!"SENT".equalsIgnoreCase(
                    offerLetter.getStatus())) {

                continue;
            }


            // Check whether notification
            // already exists

            boolean alreadyExists =
                    notificationRepository
                            .findByCandidateIdAndTypeAndReferenceId(
                                    candidate.getId(),
                                    "OFFER",
                                    offerLetter.getId()
                            )
                            .isPresent();


            // Create notification only once

            if (!alreadyExists) {

                String company =
                        offerLetter
                                .getApplication()
                                .getJob()
                                .getCompany();

                createNotification(

                        candidate,

                        "🎉 Offer Letter Received",

                        "Congratulations! You have received "
                                + "an offer letter for "
                                + offerLetter.getPosition()
                                + " at "
                                + company
                                + ".",

                        "OFFER",

                        offerLetter.getId()
                );
            }
        }
    }


    // ============================
    // Candidate Notifications
    // ============================

    public List<Notification> getMyNotifications(
            User candidate) {

        // First sync old offers
        syncExistingOfferNotifications(
                candidate
        );


        // Then return notifications

        return notificationRepository
                .findByCandidateIdOrderByCreatedAtDesc(
                        candidate.getId()
                );
    }


    // ============================
    // Unread Notifications
    // ============================

    public List<Notification> getUnreadNotifications(
            User candidate) {

        // Sync old offers first
        syncExistingOfferNotifications(
                candidate
        );


        return notificationRepository
                .findByCandidateIdAndReadStatusFalseOrderByCreatedAtDesc(
                        candidate.getId()
                );
    }


    // ============================
    // Mark Notification as Read
    // ============================

    public Notification markAsRead(
            Long notificationId,
            User candidate) {

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found."
                                )
                        );


        // Security Check

        if (!notification
                .getCandidate()
                .getId()
                .equals(candidate.getId())) {

            throw new RuntimeException(
                    "Access Denied!"
            );
        }


        notification.setReadStatus(true);


        return notificationRepository.save(
                notification
        );
    }
    // ============================
// Admin - Get All Notifications
// ============================

    public List<Notification> getAllNotifications() {

        return notificationRepository.findAll();

    }
}