package com.example.uploadfiles.service;

import com.example.uploadfiles.entity.FileStorage;
import com.example.uploadfiles.entity.enums.FileStorageStatus;
import com.example.uploadfiles.repository.FileStorageRepository;
import lombok.SneakyThrows;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;

@Service
public class FileStorageService {
    @Value("${upload.server.folder}")
    private String serverFolderPath;
    private final Hashids hashids;
    private final FileStorageRepository fileStorageRepository;

    public FileStorageService(FileStorageRepository fileStorageRepository) {
        this.hashids = new Hashids(getClass().getName(), 6);
        this.fileStorageRepository = fileStorageRepository;
    }


    public FileStorage findByHashId(String hashId){
        return fileStorageRepository.findByHashId(hashId);
    }
    public void delete(String hashId){
        FileStorage fileStorage = fileStorageRepository.findByHashId(hashId);
        File file = new File(String.format("%s/%s" , this.serverFolderPath , fileStorage.getUploadFolder()));
        if(file.delete()){
            fileStorageRepository.delete(fileStorage);
        }
    }
    @SneakyThrows
    public FileStorage save(MultipartFile multipartFile) {
        FileStorage fileStorage = new FileStorage();
        fileStorage.setName(multipartFile.getName());
        fileStorage.setFileSize(multipartFile.getSize());
        fileStorage.setContentType(multipartFile.getContentType());
        fileStorage.setFileStorageStatus(FileStorageStatus.DRAFT);
        fileStorage.setExtensions(getExt(multipartFile.getOriginalFilename()));
        fileStorageRepository.save(fileStorage);
        fileStorage.setHashId(hashids.encode(fileStorage.getId()));
        Date date = new Date();
        String path = String.format("%s/upload_files/%d/%d/%d", this.serverFolderPath,
                date.getYear() + 1900,
                date.getMonth() + 1,
                date.getDate()
        );
        File uploadFile = new File(path);
        if (uploadFile.exists() && uploadFile.mkdirs()) {
            System.out.println("Created folder");
        }


        String pathLocal = String.format("/upload_files/%d/%d/%d/%s.%s",
                date.getYear() + 1900,
                date.getMonth() + 1,
                date.getDate(),
                fileStorage.getHashId(),
                fileStorage.getExtensions()
        );
        fileStorage.setUploadFolder(pathLocal);
        fileStorageRepository.save(fileStorage);
        uploadFile = uploadFile.getAbsoluteFile();
        File file = new File(uploadFile, String.format("%s.%s",
                fileStorage.getHashId(),
                fileStorage.getExtensions())
        );
        multipartFile.transferTo(file);
        return fileStorage;
    }


    private String getExt(String name) {
        String ext = null;
        if (!name.isEmpty()) {
            int dot = name.lastIndexOf('.');
            if (dot > 0 && dot <= name.length() - 2) {
                ext = name.substring(dot + 1);
            }
        }
        return ext;
    }
}
