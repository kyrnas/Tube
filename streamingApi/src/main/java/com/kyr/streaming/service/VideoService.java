package com.kyr.streaming.service;

import com.kyr.streaming.exception.VideoNotFoundException;
import com.kyr.streaming.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface VideoService {
    byte[] getVideo(UUID id, String quality) throws VideoNotFoundException;

    byte[] getThumbnail(UUID id) throws VideoNotFoundException;

    Video saveVideo(MultipartFile file, String name) throws IOException;

    Page<Video> getAllVideoNames(int page, int size);

    Video getVideoMetadata(UUID id);

    String deleteVideo(UUID id);
}
