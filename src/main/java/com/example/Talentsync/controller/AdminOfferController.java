package com.example.Talentsync.controller;

import com.example.Talentsync.entity.OfferLetter;
import com.example.Talentsync.service.OfferLetterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/offers")
public class AdminOfferController {

    @Autowired
    private OfferLetterService offerLetterService;

    // ============================
    // Admin - Get All Offers
    // ============================

    @GetMapping
    public List<OfferLetter> getAllOffers() {

        return offerLetterService.getAllOffers();

    }
}