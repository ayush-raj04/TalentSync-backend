package com.example.Talentsync.dto;

public class DashboardResponse {

    private long applications;
    private long pending;
    private long shortlisted;
    private long rejected;

    public DashboardResponse() {
    }

    public DashboardResponse(long applications,
                             long pending,
                             long shortlisted,
                             long rejected) {

        this.applications = applications;
        this.pending = pending;
        this.shortlisted = shortlisted;
        this.rejected = rejected;
    }

    public long getApplications() {
        return applications;
    }

    public void setApplications(long applications) {
        this.applications = applications;
    }

    public long getPending() {
        return pending;
    }

    public void setPending(long pending) {
        this.pending = pending;
    }

    public long getShortlisted() {
        return shortlisted;
    }

    public void setShortlisted(long shortlisted) {
        this.shortlisted = shortlisted;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }
}