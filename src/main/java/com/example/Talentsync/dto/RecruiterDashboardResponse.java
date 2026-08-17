package com.example.Talentsync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecruiterDashboardResponse {

    private long jobsPosted;
    private long applicants;
    private long activeJobs;

}