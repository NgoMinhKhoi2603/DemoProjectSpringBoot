package com.example.DemoSpringBootTinasoft.service;

import com.example.DemoSpringBootTinasoft.entities.User;
import com.example.DemoSpringBootTinasoft.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final UserRepository userRepository;

    public ByteArrayInputStream exportUsersToExcel() throws IOException {
        String[] columns = {"ID", "Full Name", "Email", "Enabled"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            List<User> users = userRepository.findAll();
            int rowIdx = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getFullName());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.isEnabled());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public List<String> readUsersFromExcel(MultipartFile file) throws IOException {
        List<String> results = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // Bỏ qua header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Cell cellFullName = currentRow.getCell(0);
                Cell cellEmail = currentRow.getCell(1);

                String fullName = cellFullName.getStringCellValue();
                String email = cellEmail.getStringCellValue();

                results.add("Read user: " + fullName + " with email: " + email);
            }
        }
        return results;
    }
}
