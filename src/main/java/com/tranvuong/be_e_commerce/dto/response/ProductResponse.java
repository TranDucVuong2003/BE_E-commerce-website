package com.tranvuong.be_e_commerce.dto.response;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tranvuong.be_e_commerce.dto.request.ProductVariantDto;

import lombok.Data;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String categoryId;
    private Double price;
    private boolean stock;
    private String mainImage;
    private List<String> images;
    private List<ProductVariantDto> productVariants;
}