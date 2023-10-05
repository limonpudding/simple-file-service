package com.example.fileservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String host;
    private String accessKey;
    private String secretKey;
    private Long partSize;
}
