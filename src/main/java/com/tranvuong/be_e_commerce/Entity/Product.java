package com.tranvuong.be_e_commerce.Entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    String description;
    String category_id;
    Double price;
    LocalDate created_at;
    boolean stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> productVariants;

    private String mainImage;
    // Lưu trực tiếp dưới dạng List
    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> images;
}