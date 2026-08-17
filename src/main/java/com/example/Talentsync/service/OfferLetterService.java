package com.example.Talentsync.service;

import com.example.Talentsync.entity.Application;
import com.example.Talentsync.entity.ApplicationStatus;
import com.example.Talentsync.entity.OfferLetter;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.ApplicationRepository;
import com.example.Talentsync.repository.OfferLetterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OfferLetterService {

    @Autowired
    private OfferLetterRepository offerLetterRepository;

    @Autowired
    private ApplicationRepository applicationRepository;


    // ============================
    // Send Offer Letter
    // ============================

    public OfferLetter sendOfferLetter(
            Long applicationId,
            String position,
            String salary,
            String message) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User recruiter =
                (User) authentication.getPrincipal();


        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found."
                                )
                        );


        if (!application.getJob()
                .getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "Access Denied! This is not your application."
            );
        }


        if (offerLetterRepository
                .findByApplicationId(applicationId)
                .isPresent()) {

            throw new RuntimeException(
                    "Offer letter has already been sent."
            );
        }


        OfferLetter offerLetter =
                new OfferLetter();

        offerLetter.setApplication(application);

        offerLetter.setPosition(position);

        offerLetter.setSalary(salary);

        offerLetter.setMessage(message);

        offerLetter.setOfferDate(
                LocalDate.now()
        );

        offerLetter.setStatus("SENT");


        application.setStatus(
                ApplicationStatus.SELECTED
        );

        applicationRepository.save(application);


        return offerLetterRepository.save(
                offerLetter
        );
    }


    // ============================
    // Candidate View Specific Offer
    // ============================

    public OfferLetter getOfferLetterById(
            Long offerId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User candidate =
                (User) authentication.getPrincipal();


        OfferLetter offerLetter =
                offerLetterRepository
                        .findById(offerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Offer letter not found."
                                )
                        );


        if (!offerLetter
                .getApplication()
                .getCandidate()
                .getId()
                .equals(candidate.getId())) {

            throw new RuntimeException(
                    "Access Denied! This is not your offer letter."
            );
        }


        return offerLetter;
    }


    // ============================
    // Candidate View Offer
    // By Application ID
    // ============================

    public OfferLetter getOfferLetterByApplicationId(
            Long applicationId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User candidate =
                (User) authentication.getPrincipal();


        OfferLetter offerLetter =
                offerLetterRepository
                        .findByApplicationId(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Offer letter not found for this application."
                                )
                        );


        // Security Check

        if (!offerLetter
                .getApplication()
                .getCandidate()
                .getId()
                .equals(candidate.getId())) {

            throw new RuntimeException(
                    "Access Denied! This is not your offer letter."
            );
        }


        return offerLetter;
    }


    // ============================
    // Candidate Latest Offer
    // ============================

    public OfferLetter getLatestCandidateOfferLetter() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User candidate =
                (User) authentication.getPrincipal();


        List<OfferLetter> offers =
                offerLetterRepository
                        .findByApplicationCandidateIdOrderByOfferDateDesc(
                                candidate.getId()
                        );


        if (offers.isEmpty()) {

            throw new RuntimeException(
                    "No offer letter found."
            );
        }


        return offers.get(0);
    }


    // ============================
    // Admin - Get All Offers
    // ============================

    public List<OfferLetter> getAllOffers() {

        return offerLetterRepository.findAll();

    }

}