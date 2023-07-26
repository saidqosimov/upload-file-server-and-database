package com.example.uploadfiles.entity;

import com.example.uploadfiles.entity.enums.FileStorageStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String extensions;
    private String contentType;
    private Long fileSize;
    private String hashId;
    private String uploadFolder;
    private FileStorageStatus fileStorageStatus;
}
