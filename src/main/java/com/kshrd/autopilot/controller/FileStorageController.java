package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@RestController
@RequestMapping("api/v1/file")
@Tag(name = "Files")
public class FileStorageController {
    @Value("${error.url}")
    private String urlError;
    private final FileService fileService;

    public FileStorageController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload file")
    public ResponseEntity<?> saveFile(@RequestParam(required = false) MultipartFile file,
                                      HttpServletRequest request) throws IOException {
        if(file != null){
            return ResponseEntity.status(200).body(fileService.saveFile(file,request));
        }
        throw new AutoPilotException("No filename", HttpStatus.NOT_FOUND,urlError,"No filename to upload");
    }
    @GetMapping("/download/{fileName}")
    @Operation(summary = "download file")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) throws IOException {

        if(fileName.isBlank()){
            throw new AutoPilotException("No filename", HttpStatus.NOT_FOUND,urlError,"No filename to upload");
        }

        String filePath = "src/main/resources/storage/" + fileName;
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new AutoPilotException("No filename", HttpStatus.NOT_FOUND,urlError,"No filename to upload");
        }

        byte[] file = fileService.getFileContent(fileName);

        ByteArrayResource resource = new ByteArrayResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        headers.setContentType(mediaType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
    @GetMapping("")
    @Operation(summary = "Get Image")
    public ResponseEntity<Resource> getImage(
            @RequestParam("file") String fileName
    ) {
        Path path = Paths.get("src/main/resources/images/" + fileName);
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(resource.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error message " + e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

}
