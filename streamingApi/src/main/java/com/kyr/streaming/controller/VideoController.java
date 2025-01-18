package com.kyr.streaming.controller;

import com.kyr.streaming.model.Video;
import com.kyr.streaming.service.MediaConvertPollingService;
import com.kyr.streaming.service.VideoService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("video")
@AllArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final MediaConvertPollingService pollingService;

    @PostMapping()
    public ResponseEntity<UUID> saveVideo(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) throws IOException {
        Video video = videoService.saveVideo(file, name);
        return ResponseEntity.ok(video.getId());
    }

    @GetMapping("all")
    public ResponseEntity<Page<Video>> getAllVideoNames(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "40") int size){
        return ResponseEntity
                .ok(videoService.getAllVideoNames(page, size));
    }

    @GetMapping("/metadata/{id}")
    public ResponseEntity<Video> getVideoMetadata(@PathVariable("id") UUID id) {
        return ResponseEntity
                .ok(videoService.getVideoMetadata(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable("id") UUID id){
        String result = videoService.deleteVideo(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/start-polling/{videoId}")
    public String startPolling(@PathVariable UUID videoId) {
        Video video = videoService.getVideoMetadata(videoId);
        pollingService.pollJobStatus(video);
        return "Polling initiated for job ID: " + videoId.toString();
    }
}
