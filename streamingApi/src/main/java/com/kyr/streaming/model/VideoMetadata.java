package com.kyr.streaming.model;

import lombok.Data;

import java.util.UUID;

@Data
public class VideoMetadata{
    UUID id;
    String name;
    boolean processing;

    public VideoMetadata(UUID id, String name, boolean processing) {
        this.id = id;
        this.name = name;
        this.processing = processing;
    }
}
