package com.kyr.streaming.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class AwsConfig {
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .build();
    }

    @Bean
    public MediaConvertClient mediaConvertClient() {
        return MediaConvertClient.create();
    }
}
