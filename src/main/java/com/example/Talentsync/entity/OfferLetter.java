package com.example.Talentsync.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class OfferLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Which application this offer belongs to
    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;


    // Position offered by recruiter
    private String position;


    // Offered salary
    private String salary;


    // Optional message
    @Column(length = 1000)
    private String message;


    // Date on which offer was sent
    private LocalDate offerDate;


    // SENT
    private String status;


    // ==========================
    // Getters and Setters
    // ==========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}