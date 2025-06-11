package com.tranvuong.be_e_commerce.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    // Lưu file
    public String storeFile(MultipartFile file) throws IOException {
        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

         // Tạo tên file ngẫu nhiên
         String originalFilename = file.getOriginalFilename();
         String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
         String newFilename = UUID.randomUUID().toString() + fileExtension;

          // Lưu file
          Path filePath = uploadPath.resolve(newFilename);
          Files.copy(file.getInputStream(), filePath);

          return newFilename;
    }


    // Xóa file
    public void deleteFile(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename);
        Files.deleteIfExists(filePath);
    }
}
