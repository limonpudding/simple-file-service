package com.example.fileservice.api

import com.example.fileservice.model.FileDescription
import com.example.fileservice.service.MinioFileService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/files")
class FileController(val minioFileService: MinioFileService) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(
        @RequestPart("fileInfo") info: FileDescription,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<Void> {
        return try {
            minioFileService.upload(info.fileName, info.bucketName, file)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Failed to upload file: ", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping(value = ["/list/{bucketName}"])
    fun listByBucket(@PathVariable bucketName: String?): ResponseEntity<List<String>> {
        return try {
            val result: List<String> = minioFileService.getList(bucketName)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            log.error("Failed to get list of files in bucket: ", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping(value = ["/download"])
    fun download(info: FileDescription): ResponseEntity<Resource> {
        return try {
            val resource = minioFileService.download(info.fileName, info.bucketName) as ByteArrayResource
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + info.fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource)
        } catch (e: Exception) {
            log.error("Failed to upload file: ", e)
            ResponseEntity.internalServerError().build()
        }
    }
}

