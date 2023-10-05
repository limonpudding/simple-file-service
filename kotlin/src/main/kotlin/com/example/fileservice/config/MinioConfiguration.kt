package com.example.fileservice.config

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfiguration(val minioProperties: MinioProperties) {
    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(minioProperties.host)
            .credentials(minioProperties.accessKey, minioProperties.secretKey)
            .build()
    }
}
