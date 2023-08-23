package com.kyr.streaming.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Video {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private String name;

    private String originalFilename;

    private boolean processing;

    public Video (String name, String originalFilename) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.originalFilename = originalFilename;
        this.processing = true;
    }
}
