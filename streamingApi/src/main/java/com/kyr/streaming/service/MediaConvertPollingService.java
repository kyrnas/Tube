package com.kyr.streaming.service;

import com.google.gson.Gson;
import com.kyr.streaming.model.ProcessingProgress;
import com.kyr.streaming.model.Video;
import com.kyr.streaming.repository.VideoRepository;
import com.kyr.streaming.websocket.WebSocketHandlerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.GetJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.GetJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.JobStatus;

@Service
public class MediaConvertPollingService {
    private final WebSocketHandlerDelegate webSocketHandlerDelegate;
    private final MediaConvertClient mediaConvertClient;
    private final ActiveJobTracker activeJobTracker;
    private final VideoRepository videoRepository;

    @Autowired
    public MediaConvertPollingService(WebSocketHandlerDelegate webSocketHandlerDelegate, MediaConvertClient mediaConvertClient,
                                      ActiveJobTracker activeJobTracker, VideoRepository videoRepository) {
        this.webSocketHandlerDelegate = webSocketHandlerDelegate;
        this.mediaConvertClient = mediaConvertClient;
        this.activeJobTracker = activeJobTracker;
        this.videoRepository = videoRepository;
    }

    @Async
    public void pollJobStatus(Video video) {
        String videoId = video.getId().toString();
        ProcessingProgress processingProgress = new ProcessingProgress();
        Gson gson = new Gson();
        if (!video.isProcessing()) {
            processingProgress.setMessage("Processing is already complete");
            webSocketHandlerDelegate.sendProgressUpdateToSession(videoId, gson.toJson(processingProgress));
            return;
        }
        if (!activeJobTracker.addJob(videoId)) {
            processingProgress.setMessage("Polling already in progress for this video.");
            webSocketHandlerDelegate.sendProgressUpdateToSession(videoId, gson.toJson(processingProgress));
            return;
        }

        boolean isJobComplete = false;

        try {
            while (!isJobComplete) {
                Thread.sleep(5000); // Poll every 5 seconds

                GetJobResponse ldJobResponse = mediaConvertClient.getJob(GetJobRequest.builder().id(video.getLdJodId()).build());
                JobStatus ldStatus = ldJobResponse.job().status();
                processingProgress.setLd(ldStatus == JobStatus.COMPLETE || ldStatus == JobStatus.ERROR ? "100" : String.valueOf(ldJobResponse.job().jobPercentComplete()));

                GetJobResponse sdJobResponse = mediaConvertClient.getJob(GetJobRequest.builder().id(video.getSdJobId()).build());
                JobStatus sdStatus = sdJobResponse.job().status();
                processingProgress.setSd(sdStatus == JobStatus.COMPLETE || sdStatus == JobStatus.ERROR ? "100" : String.valueOf(sdJobResponse.job().jobPercentComplete()));

                GetJobResponse hdJobResponse = mediaConvertClient.getJob(GetJobRequest.builder().id(video.getHdJobId()).build());
                JobStatus hdStatus = hdJobResponse.job().status();
                processingProgress.setHd(hdStatus == JobStatus.COMPLETE || hdStatus == JobStatus.ERROR ? "100" : String.valueOf(hdJobResponse.job().jobPercentComplete()));

                GetJobResponse fullHdJobResponse = mediaConvertClient.getJob(GetJobRequest.builder().id(video.getFullHdJobId()).build());
                JobStatus fullHdStatus = fullHdJobResponse.job().status();
                processingProgress.setFullHd(fullHdStatus == JobStatus.COMPLETE || fullHdStatus == JobStatus.ERROR ? "100" : String.valueOf(fullHdJobResponse.job().jobPercentComplete()));

                webSocketHandlerDelegate.sendProgressUpdateToSession(videoId, gson.toJson(processingProgress));

                if ((ldStatus == JobStatus.COMPLETE || ldStatus == JobStatus.ERROR) && (sdStatus == JobStatus.COMPLETE || sdStatus == JobStatus.ERROR)
                    && (hdStatus == JobStatus.COMPLETE || hdStatus == JobStatus.ERROR) && (fullHdStatus == JobStatus.COMPLETE || fullHdStatus == JobStatus.ERROR)) {
                    isJobComplete = true;
                    video.setProcessing(false);
                    videoRepository.save(video);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            processingProgress.setMessage("Polling interrupted");
            webSocketHandlerDelegate.sendProgressUpdateToSession(videoId, gson.toJson(processingProgress));
        } catch (Exception e) {
            processingProgress.setMessage("Error while polling: " + e.getMessage());
            webSocketHandlerDelegate.sendProgressUpdateToSession(videoId, gson.toJson(processingProgress));
        } finally {
            activeJobTracker.removeJob(videoId); // Ensure the job is removed after completion
        }
    }
}
