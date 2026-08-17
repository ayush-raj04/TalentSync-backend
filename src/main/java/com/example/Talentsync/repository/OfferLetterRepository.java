package com.example.Talentsync.repository;

import com.example.Talentsync.entity.OfferLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferLetterRepository
        extends JpaRepository<OfferLetter, Long> {

    // Find offer letter for a particular application
    Optional<OfferLetter> findByApplicationId(
            Long applicationId
    );


    // Find all offer letters of a candidate
    List<OfferLetter> findAllByApplicationCandidateId(
            Long candidateId
    );


    // Latest offer first
    List<OfferLetter> findByApplicationCandidateIdOrderByOfferDateDesc(
            Long candidateId
    );
}