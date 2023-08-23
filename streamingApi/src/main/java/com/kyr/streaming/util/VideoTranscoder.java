package com.kyr.streaming.util;

import com.github.kokorin.jaffree.ffmpeg.*;
import com.google.gson.Gson;
import com.kyr.streaming.model.ProcessingProgress;
import com.kyr.streaming.model.Video;
import com.kyr.streaming.websocket.WebSocketHandlerDelegate;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class VideoTranscoder {
    public static void tanscodeVideo(String originalFilename, String fileExtention, Number width, Number height,
                                       Number frameRate, AtomicLong duration, WebSocketHandlerDelegate webSocketHandlerDelegate, String videoId, ProcessingProgress processingProgress) {
        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl("videos/" + videoId + "/original" + fileExtention))
                .setOverwriteOutput(true)
                .addArguments("-movflags", "faststart")
                .addOutput(UrlOutput.toUrl("videos/" + videoId + "/" + height + fileExtention).setFrameSize(width, height).setFrameRate(frameRate))
                .setProgressListener(progress -> {
                    try {
                        double percents = 100.0 * progress.getTimeMillis() / duration.get();
                        log.info("Progress {}p{}fps: {}", height, frameRate, percents);
                        if (height.intValue() == 1080) {
                            processingProgress.setFullHd(percents);
                        }
                        else if (height.intValue() == 720) {
                            processingProgress.setHd(percents);
                        }
                        else if (height.intValue() == 480) {
                            processingProgress.setSd(percents);
                        }
                        else if (height.intValue() == 360) {
                            processingProgress.setLd(percents);
                        }
                        Gson gson = new Gson();
                        webSocketHandlerDelegate.sendProgressUpdateToSession(videoId, gson.toJson(processingProgress));
                    } catch(Exception ignored) {}
                })
                .execute();
    }

    public static void createThumbnail(Video video, byte[] bytes) {
        new File("videos/" + video.getId().toString()).mkdirs();
        File tempInputFile = new File("videos/" + video.getId() + "/" + "original.mp4");

        try (OutputStream os = new FileOutputStream(tempInputFile)) {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FFmpeg.atPath()
                .addArguments("-ss", "00:00:01")
                .addInput(UrlInput.fromUrl("videos/" + video.getId() + "/" + "original.mp4"))
                .setOverwriteOutput(true)
                .addArguments("-frames:v", "1")
                .addOutput(UrlOutput.toUrl("videos/" + video.getId() + "/" + "thumbnail" + ".png").setFrameSize(1920, 1080))
                .execute();

        tempInputFile.delete();
    }
}
