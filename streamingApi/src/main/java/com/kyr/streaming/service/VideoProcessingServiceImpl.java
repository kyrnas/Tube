package com.kyr.streaming.service;

import com.kyr.streaming.model.Video;
import com.kyr.streaming.repository.VideoRepository;
import com.kyr.streaming.util.VideoTranscoder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VideoProcessingServiceImpl implements VideoProcessingService {
    private final VideoRepository videoRepository;
    private final VideoTranscoder videoTranscoder;

    @Override
    public void processVideo(Video video, String sourceFileUrl, String destFileUrl) {
        String fullHdJobId = videoTranscoder.tanscodeVideo(sourceFileUrl, destFileUrl + "/video_1080p/video", 1920, 1080, 12000000);
        String hdJobId = videoTranscoder.tanscodeVideo(sourceFileUrl, destFileUrl + "/video_720p/video", 1280, 720, 6000000);
        String sdJobId = videoTranscoder.tanscodeVideo(sourceFileUrl, destFileUrl + "/video_480p/video", 854, 480, 2500000);
        String ldJobId = videoTranscoder.tanscodeVideo(sourceFileUrl, destFileUrl + "/video_360p/video", 640, 360, 2500000);

        video.setFullHdJobId(fullHdJobId);
        video.setHdJobId(hdJobId);
        video.setSdJobId(sdJobId);
        video.setLdJodId(ldJobId);

        videoRepository.save(video);
    }
}
