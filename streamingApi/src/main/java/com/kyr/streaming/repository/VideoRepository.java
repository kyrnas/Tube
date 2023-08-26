package com.kyr.streaming.repository;

import com.kyr.streaming.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface VideoRepository extends CrudRepository<Video, UUID> {
    Video findByName(String name);

    boolean existsByName(String name);

    Page<Video> findAll(Pageable pageable);

    Video findVideoById(UUID id);
}
