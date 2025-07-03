package com.tranvuong.be_e_commerce.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private String category_id;
    private List<ProductVariantDto> productVariants;
    private Double price;
    private boolean stock;
    private List<String> images;
} 

