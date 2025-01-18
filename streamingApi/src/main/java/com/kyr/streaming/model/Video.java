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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private String name;

    @Column(name = "original_filename")
    private String originalFilename;

    private boolean processing;

    @Column(name = "full_hd_job_id")
    private String fullHdJobId;

    @Column(name = "hd_job_id")
    private String hdJobId;

    @Column(name = "sd_job_id")
    private String sdJobId;

    @Column(name = "ld_job_id")
    private String ldJodId;

    public Video (String name, String originalFilename) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.originalFilename = originalFilename;
        this.processing = true;
    }
}
