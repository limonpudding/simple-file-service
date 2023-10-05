package com.example.fileservice.service

import com.example.fileservice.config.MinioProperties
import io.minio.*
import io.minio.errors.MinioException
import io.minio.messages.Item
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

@Service
class MinioFileService(
    val minioClient: MinioClient,
    val minioProperties: MinioProperties
) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun upload(name: String?, bucket: String?, file: MultipartFile) {
        try {

            // Создаем бакет, если такого еще нет
            val found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
                log.info("Created new bucket $bucket.")
            }

            // Отправляем файл в указанынй бакет
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(name)
                    .stream(file.inputStream, file.size, minioProperties.partSize)
                    .build()
            )
            log.info("Object $name is successfully uploaded to bucket $bucket.")
        } catch (e: MinioException) {
            log.error("Error occurred: $e")
            log.debug("HTTP trace: " + e.httpTrace())
        }
    }

    @Throws(Exception::class)
    fun getList(bucketName: String?): List<String> {
        val iterator: Iterator<Result<Item>> = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .recursive(true)
                .build()
        ).iterator()
        val names: MutableList<String> = ArrayList()
        while (iterator.hasNext()) {
            val result = iterator.next()
            names.add(result.get().objectName())
        }
        return names
    }

    @Throws(Exception::class)
    fun download(name: String?, bucket: String?): Resource {
        val response = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucket)
                .`object`(name)
                .build()
        )
        return ByteArrayResource(response.readAllBytes())
    }
}