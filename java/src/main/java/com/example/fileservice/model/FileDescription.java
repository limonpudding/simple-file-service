package com.example.fileservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDescription {

    private String fileName;
    private String bucketName;
}
