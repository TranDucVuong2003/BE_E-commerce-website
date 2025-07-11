package com.tranvuong.be_e_commerce.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.tranvuong.be_e_commerce.Services.FileCloudinaryService;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

@RestController
@RequestMapping("/file")
public class CloudinaryController {

    @Autowired
    FileCloudinaryService fileCloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseData> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            ResponseData url = fileCloudinaryService.uploadFile(file);
            return ResponseEntity.status(url.getStatusCode()).body(url);
        } catch (Exception e) {
            e.printStackTrace(); // Thêm dòng này để xem lỗi chi tiết trên console
            ResponseData errorResponse = new ResponseData("Upload failed", 500, 1, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/delete-image")
    public ResponseEntity<ResponseData> deleteImage(@RequestParam("url") String imageUrl) {
        try {
            boolean deleted = fileCloudinaryService.deleteFileFromCloudinary(imageUrl);
            if (deleted) {
                return ResponseEntity.ok(new ResponseData("Image deleted from Cloudinary", 200, 0, null));
            } else {
                return ResponseEntity.status(404).body(new ResponseData("Image not found or could not be deleted", 404, 1, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseData("Failed to delete image: " + e.getMessage(), 500, 1, null));
        }
    }
}
