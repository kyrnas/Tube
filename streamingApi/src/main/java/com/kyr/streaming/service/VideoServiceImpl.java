package com.kyr.streaming.service;


import com.kyr.streaming.exception.UknownQualityRequestedException;
import com.kyr.streaming.exception.VideoAlreadyExistsException;
import com.kyr.streaming.exception.VideoNotFoundException;
import com.kyr.streaming.model.Video;
import com.kyr.streaming.repository.VideoRepository;
import com.kyr.streaming.util.VideoTranscoder;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoProcessingService videoProcessingService;

    @Autowired
    private VideoTranscoder videoTranscoder;

    @Autowired
    private S3Client s3Client;

    @Value("${aws.source.bucket.name}")
    private String sourceBucketName;

    @Value("${aws.videos.bucket.name}")
    private String videosBucketName;

    @Override
    public Video saveVideo(MultipartFile file, String name) throws IOException {
        if (videoRepository.existsByName(name)) {
            throw new VideoAlreadyExistsException();
        }
        Video video = new Video(name, file.getOriginalFilename());
        video = videoRepository.save(video);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(sourceBucketName)
                .key(video.getId().toString() + "/" + file.getOriginalFilename())
                .build();
        String sourceFile = "s3://" + sourceBucketName + "/" + video.getId().toString() + "/" + file.getOriginalFilename();
        String destFile = "s3://" + videosBucketName + "/" + video.getId().toString();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        videoProcessingService.processVideo(video, sourceFile, destFile);
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
