package com.kyr.streaming.repository;

import com.kyr.streaming.model.Video;
import com.kyr.streaming.model.VideoMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface VideoRepository extends CrudRepository<Video, UUID> {
    Video findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT new com.kyr.streaming.model.VideoMetadata(v.id, v.name, v.processing) FROM Video v")
    Page<VideoMetadata> getAllNames(Pageable pageable);

    @Query("SELECT new com.kyr.streaming.model.VideoMetadata(v.id, v.name, v.processing) FROM Video v WHERE v.id = ?1")
    VideoMetadata getVideoMetadata(UUID id);
}
