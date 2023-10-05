package com.example.fileservice.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "minio")
data class MinioProperties(
    val host: String,
    val accessKey: String,
    val secretKey: String,
    val partSize: Long
)


