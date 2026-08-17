package com.example.Talentsync.service;

import com.example.Talentsync.dto.JobRequest;
import com.example.Talentsync.entity.Job;
import com.example.Talentsync.entity.User;
import com.example.Talentsync.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;


    // ============================
    // Recruiter Post Job
    // ============================

    public Job addJob(JobRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User recruiter =
                (User) authentication.getPrincipal();

        Job job = new Job();

        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setExperience(request.getExperience());
        job.setSkills(request.getSkills());
        job.setDescription(request.getDescription());

        job.setRecruiter(recruiter);

        return jobRepository.save(job);
    }


    // ============================
    // All Jobs
    // Latest Job First
    // ============================

    public List<Job> getAllJobs() {

        return jobRepository.findAllByOrderByIdDesc();
    }


    // ============================
    // Logged-in Recruiter's Jobs
    // ============================

    public List<Job> getMyJobs() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User recruiter =
                (User) authentication.getPrincipal();

        return jobRepository
                .findByRecruiterId(recruiter.getId());
    }


    // ============================
    // Job By Id
    // ============================

    public Job getJobById(Long id) {

        return jobRepository
                .findById(id)
                .orElse(null);
    }


    // ============================
    // Delete Job
    // ============================

    public void deleteJob(Long id) {

        jobRepository.deleteById(id);
    }


    // ============================
    // Admin - Get Companies
    // ============================

    public List<String> getCompanies() {

        List<Job> jobs =
                jobRepository.findAll();

        /*
         * Microsoft aur microsoft
         * ko same company maana jayega.
         */

        Map<String, String> companies =
                new TreeMap<>(
                        String.CASE_INSENSITIVE_ORDER
                );

        for (Job job : jobs) {

            String company = job.getCompany();

            if (company != null &&
                    !company.trim().isEmpty()) {

                String cleanCompany =
                        company.trim();

                companies.putIfAbsent(
                        cleanCompany,
                        cleanCompany
                );
            }
        }

        return new ArrayList<>(
                companies.values()
        );
    }


    // ============================
    // Admin - Get Company Jobs
    // ============================

    public List<Job> getJobsByCompany(
            String company) {

        return jobRepository
                .findByCompanyIgnoreCase(
                        company.trim()
                );
    }

}