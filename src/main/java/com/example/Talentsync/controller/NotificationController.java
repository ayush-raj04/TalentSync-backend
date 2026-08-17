package com.example.Talentsync.controller;

import com.example.Talentsync.entity.Notification;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    // ============================
    // Get My Notifications
    // ============================

    @GetMapping
    public List<Notification> getMyNotifications() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User candidate =
                (User) authentication.getPrincipal();

        return notificationService
                .getMyNotifications(candidate);
    }


    // ============================
    // Get Unread Notifications
    // ============================

    @GetMapping("/unread")
    public List<Notification> getUnreadNotifications() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User candidate =
                (User) authentication.getPrincipal();

        return notificationService
                .getUnreadNotifications(candidate);
    }


    // ============================
    // Mark Notification as Read
    // ============================

    @PutMapping("/{notificationId}/read")
    public Notification markAsRead(
            @PathVariable Long notificationId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User candidate =
                (User) authentication.getPrincipal();

        return notificationService
                .markAsRead(
                        notificationId,
                        candidate
                );
    }
}