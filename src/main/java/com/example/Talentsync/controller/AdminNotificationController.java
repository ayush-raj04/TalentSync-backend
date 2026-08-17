package com.example.Talentsync.controller;

import com.example.Talentsync.entity.Notification;
import com.example.Talentsync.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;


    // ============================
    // Admin - Get All Notifications
    // ============================

    @GetMapping
    public List<Notification> getAllNotifications() {

        return notificationService.getAllNotifications();

    }
}