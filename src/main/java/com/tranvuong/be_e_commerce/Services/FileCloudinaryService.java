package com.tranvuong.be_e_commerce.Services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

@Service
public class FileCloudinaryService {
    
    @Autowired
    private Cloudinary cloudinary;

    private static final String UPLOAD_PRESET = "upload_img";
    public ResponseData uploadFile(MultipartFile file) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("upload_preset", UPLOAD_PRESET);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        String url = uploadResult.get("secure_url").toString();

        return new ResponseData("Upload successful", 200,0,url);
    }

    public boolean deleteFileFromCloudinary(String imageUrl) throws IOException {
        String publicId = extractPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            Object res = result.get("result");
            return "ok".equals(res); // true nếu xóa thành công
        }
        return false;
    }
    
    // Hàm hỗ trợ lấy public_id từ url Cloudinary
    private String extractPublicIdFromUrl(String url) {
        // Ví dụ: https://res.cloudinary.com/dyyl5ovel/image/upload/v1719999999/abc_xyz.jpg
        // public_id là phần sau /upload/ và trước phần mở rộng .jpg
        try {
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1];
                // Bỏ version nếu có (v...)
                if (path.startsWith("v")) {
                    int slashIndex = path.indexOf('/');
                    if (slashIndex > 0) {
                        path = path.substring(slashIndex + 1);
                    }
                }
                // Bỏ phần mở rộng (.jpg, .png, ...)
                int dotIndex = path.lastIndexOf('.');
                if (dotIndex > 0) {
                    return path.substring(0, dotIndex);
                } else {
                    return path;
                }
            }
        } catch (Exception e) {
            // log lỗi nếu cần
        }
        return null;
    }
}
