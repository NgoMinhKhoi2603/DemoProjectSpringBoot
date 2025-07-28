package com.example.DemoSpringBootTinasoft.controller;

import com.example.DemoSpringBootTinasoft.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        // Tạo URL để truy cập file
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();

        return ResponseEntity.ok(Map.of(
                "fileName", fileName,
                "fileDownloadUri", fileDownloadUri
        ));
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Xác định content type của file
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // fallback to the default content type if type could not be determined
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/export/sample.txt")
    public ResponseEntity<Resource> exportSampleFile() {
        String content = "Đây là nội dung của file mẫu.\nĐược tạo vào lúc " + java.time.LocalDateTime.now();

        // Chuyển String thành Resource
        Resource resource = new org.springframework.core.io.ByteArrayResource(content.getBytes());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                // Header này yêu cầu trình duyệt tải file xuống thay vì hiển thị
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sample.txt\"")
                .body(resource);
    }
}
