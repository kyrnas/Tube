package com.kyr.streaming.service;

import com.github.kokorin.jaffree.ffmpeg.*;
import com.google.gson.Gson;
import com.kyr.streaming.model.ProcessingProgress;
import com.kyr.streaming.model.Video;
import com.kyr.streaming.repository.VideoRepository;
import com.kyr.streaming.util.VideoTranscoder;
import com.kyr.streaming.websocket.WebSocketHandlerDelegate;
import lombok.AllArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
public class VideoProcessingServiceImpl implements VideoProcessingService {
    private final VideoRepository videoRepository;
    private final TaskExecutor taskExecutor; // Inject TaskExecutor or any other queue management component
    private final WebSocketHandlerDelegate webSocketHandlerDelegate;


    @Override
    public void processVideo(Video video, byte[] bytes) {
        // Add the video processing task to the task queue
        taskExecutor.execute(() -> processVideoAsync(video, bytes));
    }

    private void processVideoAsync(Video video, byte[] bytes) {
        String fileExtention = "." + video.getOriginalFilename().split("\\.")[video.getOriginalFilename().split("\\.").length - 1];
        String filename = video.getOriginalFilename().split("\\.")[0];
        File tempInputFile = new File("videos/" + video.getId() + "/original" + fileExtention);

        try (OutputStream os = new FileOutputStream(tempInputFile)) {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final AtomicLong duration = new AtomicLong();
        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl("videos/" + video.getId() + "/original" + fileExtention))
                .setOverwriteOutput(true)
                .addOutput(new NullOutput())
                .setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        duration.set(progress.getTimeMillis());
                    }
                })
                .execute();

        ProcessingProgress progress = new ProcessingProgress();
        Gson gson = new Gson();

        VideoTranscoder.tanscodeVideo(filename, fileExtention, 1920, 1080, 60, duration, webSocketHandlerDelegate, video.getId().toString(), progress);
        progress.setFullHd(100.);
        VideoTranscoder.tanscodeVideo(filename, fileExtention, 1280, 720, 60, duration, webSocketHandlerDelegate, video.getId().toString(), progress);
        progress.setHd(100.);
        VideoTranscoder.tanscodeVideo(filename, fileExtention, 854, 480, 30, duration, webSocketHandlerDelegate, video.getId().toString(), progress);
        progress.setSd(100.);
        VideoTranscoder.tanscodeVideo(filename, fileExtention, 640, 360, 24, duration, webSocketHandlerDelegate, video.getId().toString(), progress);
        progress.setLd(100.);

        // update the client
        webSocketHandlerDelegate.sendProgressUpdateToSession(video.getId().toString(), gson.toJson(progress));

        tempInputFile.delete();

        // Set the processing status to completed when processing is finished
        video.setProcessing(false);
        videoRepository.save(video);
    }
}
