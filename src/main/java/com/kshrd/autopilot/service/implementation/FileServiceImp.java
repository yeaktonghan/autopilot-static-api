package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.config.FileStorageProperties;
import com.kshrd.autopilot.entities.FileStorage;
import com.kshrd.autopilot.entities.request.FileRequest;
import com.kshrd.autopilot.repository.FileRepository;
import com.kshrd.autopilot.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImp implements FileService {
    private final FileRepository fileRepository;
    private final FileStorageProperties fileStorageProperties;

    public FileServiceImp(FileRepository fileRepository, FileStorageProperties fileStorageProperties) {
        this.fileRepository = fileRepository;
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public FileRequest saveFile(MultipartFile file, HttpServletRequest request) throws IOException {
        FileStorage obj = new FileStorage();
        obj.setFileName(file.getOriginalFilename());
        obj.setFileType(file.getContentType());
        obj.setSize(file.getSize());
        obj.setFileUrl(String.valueOf(request.getRequestURL()).substring(0, 22) + "api/v1/file?file=" + obj.getFileName());
        String uploadPath = fileStorageProperties.getUploadPath();
        Path directoryPath = Paths.get(uploadPath).toAbsolutePath().normalize();
        java.io.File directory = directoryPath.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        File dest = new File(directoryPath.toFile(), fileName);
        file.transferTo(dest);
        fileRepository.save(obj);
        return new FileRequest(obj.getFileName(), obj.getFileUrl(), obj.getFileType(), obj.getSize());
    }
//    @Override
//    public List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException {
//        List<FileRequest> filesResponses = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//            String uploadPath = fileStorageProperties.getUploadPath();
//            Path directoryPath = Paths.get(uploadPath).toAbsolutePath().normalize();
//            java.io.File directory = directoryPath.toFile();
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//            String fileName = file.getOriginalFilename();
//            File dest = new File(directoryPath.toFile(), fileName);
//            file.transferTo(dest);
//
//            FileStorage obj = new FileStorage();
//            obj.setFileName(file.getOriginalFilename());
//            obj.setFileType(file.getContentType());
//            obj.setSize(file.getSize());
//            obj.setFileUrl(String.valueOf(request.getRequestURL()).substring(0, 22) + "images/" + obj.getFileName());
//            fileRepository.save(obj);
//            filesResponses.add(new FileRequest(obj.getFileName(), obj.getFileUrl(), obj.getFileType(), obj.getSize()));
//        }
//        return filesResponses;
//    }

    @Override
    public byte[] getFileContent(String fileName) throws IOException {
        String uploadPath = fileStorageProperties.getUploadPath();
        Path path = Paths.get(uploadPath + fileName);
        ByteArrayResource file = new ByteArrayResource(Files.readAllBytes(path));
        return file.getContentAsByteArray();
    }
}
