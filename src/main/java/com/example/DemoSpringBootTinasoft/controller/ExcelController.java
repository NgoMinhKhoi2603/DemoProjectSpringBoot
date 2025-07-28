package com.example.DemoSpringBootTinasoft.controller;

import com.example.DemoSpringBootTinasoft.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping("/export/users")
    public ResponseEntity<Resource> exportUsers() throws IOException {
        String filename = "users.xlsx";
        InputStreamResource file = new InputStreamResource(excelService.exportUsersToExcel());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/import/users")
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            List<String> results = excelService.readUsersFromExcel(file);
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to import data: " + e.getMessage());
        }
    }
}
