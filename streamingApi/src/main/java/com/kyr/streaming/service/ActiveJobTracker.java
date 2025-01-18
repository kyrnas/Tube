package com.kyr.streaming.service;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveJobTracker {

    private final ConcurrentHashMap<String, Boolean> activeJobs = new ConcurrentHashMap<>();

    public boolean isJobActive(String jobId) {
        return activeJobs.containsKey(jobId);
    }

    public boolean addJob(String jobId) {
        return activeJobs.putIfAbsent(jobId, true) == null;
    }

    public void removeJob(String jobId) {
        activeJobs.remove(jobId);
    }
}

