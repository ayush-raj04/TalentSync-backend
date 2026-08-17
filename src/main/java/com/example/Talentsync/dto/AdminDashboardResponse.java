package com.example.Talentsync.dto;

public class AdminDashboardResponse {

    private long totalUsers;
    private long candidates;
    private long recruiters;
    private long pendingApprovals;

    public AdminDashboardResponse(
            long totalUsers,
            long candidates,
            long recruiters,
            long pendingApprovals) {

        this.totalUsers = totalUsers;
        this.candidates = candidates;
        this.recruiters = recruiters;
        this.pendingApprovals = pendingApprovals;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getCandidates() {
        return candidates;
    }

    public long getRecruiters() {
        return recruiters;
    }

    public long getPendingApprovals() {
        return pendingApprovals;
    }
}