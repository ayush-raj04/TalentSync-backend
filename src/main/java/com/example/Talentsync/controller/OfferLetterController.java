package com.example.Talentsync.controller;

import com.example.Talentsync.entity.OfferLetter;
import com.example.Talentsync.service.OfferLetterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OfferLetterController {

    @Autowired
    private OfferLetterService offerLetterService;


    // ============================
    // Recruiter Send Offer Letter
    // ============================

    @PostMapping("/api/offers/send/{applicationId}")
    public OfferLetter sendOfferLetter(
            @PathVariable Long applicationId,
            @RequestParam String position,
            @RequestParam String salary,
            @RequestParam(required = false) String message) {

        return offerLetterService.sendOfferLetter(
                applicationId,
                position,
                salary,
                message
        );
    }


    // ============================
    // Candidate View Specific Offer
    // By Offer ID
    // ============================

    @GetMapping("/api/offers/my/{offerId}")
    public OfferLetter getOfferLetterById(
            @PathVariable Long offerId) {

        return offerLetterService
                .getOfferLetterById(offerId);
    }


    // ============================
    // Candidate View Specific Offer
    // By Application ID
    // ============================

    @GetMapping("/api/offers/application/{applicationId}")
    public OfferLetter getOfferLetterByApplicationId(
            @PathVariable Long applicationId) {

        return offerLetterService
                .getOfferLetterByApplicationId(
                        applicationId
                );
    }


    // ============================
    // Candidate Latest Offer
    // ============================

    @GetMapping("/api/offers/my")
    public OfferLetter getMyOfferLetter() {

        return offerLetterService
                .getLatestCandidateOfferLetter();
    }

}