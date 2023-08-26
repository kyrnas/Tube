package com.kyr.streaming.service;


import com.kyr.streaming.exception.UknownQualityRequestedException;
import com.kyr.streaming.exception.VideoAlreadyExistsException;
import com.kyr.streaming.exception.VideoNotFoundException;
import com.kyr.streaming.model.Video;
import com.kyr.streaming.repository.VideoRepository;
import com.kyr.streaming.util.VideoTranscoder;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VideoServiceImpl implements VideoService {
    private VideoRepository videoRepository;
    private VideoProcessingService videoProcessingService;

    @Override
    public byte[] getVideo(UUID id, String quality) throws VideoNotFoundException {
        if (videoRepository.findById(id).isEmpty()){
            throw new VideoNotFoundException();
        }
        File reencodedVideo;
        byte[] result;
        switch (quality) {
            case "1080p":
                reencodedVideo = new File("videos/" + id.toString() + "/1080" + ".mp4");
                try (InputStream is = new FileInputStream(reencodedVideo)) {
                    result = is.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "720p":
                reencodedVideo = new File("videos/" + id.toString() + "/720" + ".mp4");
                try (InputStream is = new FileInputStream(reencodedVideo)) {
                    result = is.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "480p":
                reencodedVideo = new File("videos/" + id.toString() + "/480" + ".mp4");
                try (InputStream is = new FileInputStream(reencodedVideo)) {
                    result = is.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "360p":
                reencodedVideo = new File("videos/" + id.toString() + "/360" + ".mp4");
                try (InputStream is = new FileInputStream(reencodedVideo)) {
                    result = is.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new UknownQualityRequestedException();
        }
        return result;
    }

    @Override
    public byte[] getThumbnail(UUID id) throws VideoNotFoundException {
        if (videoRepository.findById(id).isEmpty()){
            throw new VideoNotFoundException();
        }
        File thumbnailFile = new File("videos/" + id + "/" + "thumbnail" + ".png");
        byte[] result;
        try (InputStream is = new FileInputStream(thumbnailFile)) {
            result = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Video saveVideo(MultipartFile file, String name) throws IOException {
        if (videoRepository.existsByName(name)) {
            throw new VideoAlreadyExistsException();
        }
        Video video = new Video(name, file.getOriginalFilename());
        VideoTranscoder.createThumbnail(video, file.getBytes());
        video = videoRepository.save(video);
        videoProcessingService.processVideo(video, file.getBytes());
        return video;
    }

    @Override
    public Page<Video> getAllVideoNames(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        return videoRepository.findAll(paging);
    }

    @Override
    public Video getVideoMetadata(UUID id){
        return videoRepository.findVideoById(id);
    }

    @Override
    public String deleteVideo(UUID id) {
        Optional<Video> instance = videoRepository.findById(id);
        if (instance.isPresent()) {
            videoRepository.delete(instance.get());
            try {
                FileUtils.deleteDirectory(new File("videos/" + id));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return instance.get().getName();
        }
        return "Not Found...";
    }
}
