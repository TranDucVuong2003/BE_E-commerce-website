package com.tranvuong.be_e_commerce.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranvuong.be_e_commerce.Entity.Product;
import com.tranvuong.be_e_commerce.Services.FileCloudinaryService;

import com.tranvuong.be_e_commerce.Services.Impl.ProductServiceImp;
import com.tranvuong.be_e_commerce.dto.request.ProductRequest;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;
import com.tranvuong.be_e_commerce.dto.request.ProductVariantDto;
import com.tranvuong.be_e_commerce.Repository.ProductRepository;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductServiceImp productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileCloudinaryService fileCloudinaryService;

    // Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<ResponseData> getAllProducts() {
        ResponseData products = productService.getAllProducts();
        return ResponseEntity.status(products.getStatusCode()).body(products);
    }

    // Lấy sản phẩm theo id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getProductById(@PathVariable String id) {
        ResponseData response = productService.getProductById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Tạo sản phẩm mới
    @PostMapping
    public ResponseEntity<ResponseData> createProduct(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category_id", required = false) String categoryId,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "stock", required = false) Boolean stock,
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestParam(value = "images", required = false) List<MultipartFile> files,
            @RequestParam(value = "variants", required = false) String variantsJson) {
        try {

            // Lưu ảnh
            List<String> imageNames = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        String contentType = file.getContentType();
                        if (contentType == null || !contentType.startsWith("image/")) {
                            return ResponseEntity.badRequest()
                                    .body(new ResponseData("Only image files are allowed", 400, 400, null));
                        }
                        String fileUrl = fileCloudinaryService.uploadFile(file).getData().toString();
                        imageNames.add(fileUrl);
                    }
                }
            }

            String mainImageUrl = null;
            if (mainImage != null && !mainImage.isEmpty()) {
                mainImageUrl = fileCloudinaryService.uploadFile(mainImage).getData().toString();
            }

            // Parse variantsJson thành List<ProductVariantDto>
            ObjectMapper objectMapper = new ObjectMapper();
            List<ProductVariantDto> productVariants = objectMapper.readValue(
                    variantsJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ProductVariantDto.class));

            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(name);
            productRequest.setDescription(description);
            productRequest.setCategory_id(categoryId);
            productRequest.setPrice(price);
            if (stock == null) {
                stock = false; // hoặc true, tùy logic của bạn
            }
            productRequest.setStock(stock);
            if (mainImageUrl != null) {
                productRequest.setMainImage(mainImageUrl);
            }
            productRequest.setImages(imageNames);
            productRequest.setProductVariants(productVariants);

            ResponseData response = productService.createdProduct(productRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseData("Failed to create product: " + e.getMessage(), 400, 400, null));
        }
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> updateProductWithImages(
            @PathVariable String id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category_id", required = false) String categoryId,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "stock", required = false) Boolean stock,
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestParam(value = "images", required = false) List<MultipartFile> files,
            @RequestParam(value = "variants", required = false) String variantsJson) {
        try {
            java.util.Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ResponseData("Product not found", 404, 404, null));
            }
            Product product = productOpt.get();

            // Xử lý images
            List<String> imageUrls = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                // Xóa ảnh cũ trên Cloudinary trước khi upload ảnh mới
                if (product.getImages() != null) {
                    for (String oldImage : product.getImages()) {
                        try {
                            fileCloudinaryService.deleteFileFromCloudinary(oldImage);
                        } catch (IOException e) {
                            System.err.println("Failed to delete old image from Cloudinary: " + oldImage);
                        }
                    }
                }
                
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        String contentType = file.getContentType();
                        if (contentType == null || !contentType.startsWith("image/")) {
                            return ResponseEntity.badRequest()
                                    .body(new ResponseData("Only image files are allowed", 400, 400, null));
                        }
                        String fileUrl = fileCloudinaryService.uploadFile(file).getData().toString();
                        imageUrls.add(fileUrl);
                    }
                }
            } else {
                imageUrls = product.getImages();
            }

            // Xử lý mainImage
            String mainImageUrl = null;
            if (mainImage != null && !mainImage.isEmpty()) {
                // Xóa ảnh chính cũ trên Cloudinary
                if (product.getMainImage() != null) {
                    try {
                        fileCloudinaryService.deleteFileFromCloudinary(product.getMainImage());
                    } catch (IOException e) {
                        System.err.println("Failed to delete old main image from Cloudinary: " + product.getMainImage());
                    }
                }
                mainImageUrl = fileCloudinaryService.uploadFile(mainImage).getData().toString();
            } else {
                mainImageUrl = product.getMainImage();
            }

            // Xử lý variants
            ObjectMapper objectMapper = new ObjectMapper();
            List<ProductVariantDto> productVariants = new ArrayList<>();
            if (variantsJson != null && !variantsJson.isEmpty()) {
                productVariants = objectMapper.readValue(
                        variantsJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ProductVariantDto.class)
                );
            } else {
                // Chuyển List<ProductVariant> sang List<ProductVariantDto>
                if (product.getProductVariants() != null) {
                    productVariants = product.getProductVariants().stream()
                        .map(variant -> {
                            ProductVariantDto dto = new ProductVariantDto();
                            dto.setSize(variant.getSize());
                            dto.setQuantity(variant.getQuantity());
                            return dto;
                        }).collect(java.util.stream.Collectors.toList());
                }
            }

            // Tạo ProductRequest với logic giữ nguyên trường cũ nếu không truyền lên
            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(name != null ? name : product.getName());
            productRequest.setDescription(description != null ? description : product.getDescription());
            productRequest.setCategory_id(categoryId != null ? categoryId : product.getCategory_id());
            productRequest.setPrice(price != null ? price : product.getPrice());
            productRequest.setStock(stock != null ? stock : product.isStock());
            productRequest.setMainImage(mainImageUrl);
            productRequest.setImages(imageUrls);
            productRequest.setProductVariants(productVariants);

            ResponseData response = productService.updateProduct(productRequest, id);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseData("Failed to update product: " + e.getMessage(), 400, 400, null));
        }
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> deleteProduct(@PathVariable String id) {
        try {
            // Lấy Product entity trực tiếp từ repository
            java.util.Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ResponseData("Product not found", 404, 404, null));
            }
            Product product = productOpt.get();
            List<String> images = product.getImages();

            // Xóa sản phẩm
            ResponseData response = productService.deleteProduct(id);
            if (response.getStatusCode() == 200) {
                // Xóa các file ảnh trên Cloudinary
                if (images != null) {
                    for (String image : images) {
                        try {
                            fileCloudinaryService.deleteFileFromCloudinary(image);
                        } catch (IOException e) {
                            System.err.println("Failed to delete image from Cloudinary: " + image);
                        }
                    }
                }
                // Xóa mainImage trên Cloudinary nếu có
                if (product.getMainImage() != null) {
                    try {
                        fileCloudinaryService.deleteFileFromCloudinary(product.getMainImage());
                    } catch (IOException e) {
                        System.err.println("Failed to delete main image from Cloudinary: " + product.getMainImage());
                    }
                }
            }
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseData("Failed to delete product: " + e.getMessage(), 400, 400, null));
        }
    }

}
