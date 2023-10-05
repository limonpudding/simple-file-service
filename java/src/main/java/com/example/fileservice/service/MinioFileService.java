package com.example.fileservice.service;

import com.example.fileservice.config.MinioProperties;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public void upload(String name, String bucket, MultipartFile file)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {

            // Создаем бакет, если такого еще нет
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created new bucket {}.", bucket);
            }

            // Отправляем файл в указанынй бакет
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(name)
                            .stream(file.getInputStream(), file.getSize(), minioProperties.getPartSize())
                            .build());
            log.info("Object {} is successfully uploaded to bucket {}.", name, bucket);
        } catch (MinioException e) {
            log.error("Error occurred: " + e);
            log.debug("HTTP trace: " + e.httpTrace());
        }
    }

    public List<String> getList(String bucketName) throws Exception {
        Iterator<Result<Item>> iterator = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build()
        ).iterator();
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) {
            Result<Item> result = iterator.next();
            names.add(result.get().objectName());
        }
        return names;
    }

    public Resource download(String name, String bucket) throws Exception {
        GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(name)
                .build());
        return new ByteArrayResource(response.readAllBytes());
    }
}