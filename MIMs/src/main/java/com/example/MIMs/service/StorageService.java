package com.example.MIMs.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.MIMs.entity.FileData;
import com.example.MIMs.repository.FileDataRepository;


@Service
public class StorageService {


    @Autowired
    private FileDataRepository fileDataRepository;

    private final String FOLDER_PATH="C:/Users/Andre/Desktop/MyMiMFiles";

    public String uploadImageToFileSystem(MultipartFile file) throws IOException {
        String filePath=FOLDER_PATH+file.getOriginalFilename();
        
        FileData fileData = fileDataRepository.save(FileData.builder()
            .name(file.getOriginalFilename())
            .type(file.getContentType())
            .filePath(filePath).build());

        file.transferTo(new File(filePath));

        try{
            if (fileData != null) {
                return "File uploaded successfully: " + filePath;
            }
        } catch (Exception e) {
            System.err.println("File upload failed:" + e.getMessage());  
        }
        
        return "Failed to Upload";
    }

    public byte [] downloadImageFromFileSystem(String fileName) throws IOException{
        Optional<FileData> fileData = fileDataRepository.findByName(fileName);
        String filePath=fileData.get().getFilePath();
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }

}
