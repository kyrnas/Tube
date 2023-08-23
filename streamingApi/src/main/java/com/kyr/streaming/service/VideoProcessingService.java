package com.kyr.streaming.service;

import com.kyr.streaming.model.Video;

public interface VideoProcessingService {
    public void processVideo(Video video, byte[] bytes);
}
