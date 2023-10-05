package com.example.fileservice.api;

import com.example.fileservice.model.FileDescription;
import com.example.fileservice.service.MinioFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioFileService minioFileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadFile(@RequestPart("fileInfo") FileDescription info, @RequestPart("file") MultipartFile file) {
        try {
            minioFileService.upload(info.getFileName(), info.getBucketName(), file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to upload file: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/list/{bucketName}")
    public ResponseEntity<List<String>> listByBucket(@PathVariable String bucketName) {
        try {
            List<String> result = minioFileService.getList(bucketName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to get list of files in bucket: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/download")
    public ResponseEntity<Resource> download(FileDescription info) {
        try {
            ByteArrayResource resource = (ByteArrayResource) minioFileService.download(info.getFileName(), info.getBucketName());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + info.getFileName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            log.error("Failed to upload file: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
