package com.kyr.streaming.controller;

import com.kyr.streaming.model.Video;
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
    private VideoService videoService;

    @PostMapping()
    public ResponseEntity<UUID> saveVideo(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) throws IOException {
        Video video = videoService.saveVideo(file, name);
        return ResponseEntity.ok(video.getId());
    }

    @GetMapping("{id}")
    public ResponseEntity<Resource> getVideoById(@PathVariable("id") UUID id, @RequestParam(value = "quality", defaultValue = "1080p") String quality) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(videoService.getVideo(id, quality)));
    }

    @GetMapping("/thumbnail/{id}")
    public ResponseEntity<Resource> getVideoThumbnail(@PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(videoService.getThumbnail(id)));
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
}
