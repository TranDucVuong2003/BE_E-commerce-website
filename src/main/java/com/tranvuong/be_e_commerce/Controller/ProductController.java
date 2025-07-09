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
import com.tranvuong.be_e_commerce.Services.FileStorageService;
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
    private FileStorageService fileStorageService;

    @Autowired
    private ProductRepository productRepository;

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
            @RequestParam(value = "mainImage", required = false) MultipartFile mainImageFile,
            @RequestParam(value = "images", required = false) List<MultipartFile> files,
            @RequestParam(value = "variants", required = false) String variantsJson) {
        try {

            // Lưu ảnh
            List<String> imageNames = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.badRequest()
                                .body(new ResponseData("Only image files are allowed", 400, 400, null));
                    }
                    String fileName = fileStorageService.storeFile(file);
                    imageNames.add(fileName);
                }
            }

            // Lưu ảnh chính
            String mainImageName = fileStorageService.storeFile(mainImageFile);

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
            productRequest.setStock(stock);
            productRequest.setMainImage(mainImageName);
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
            @RequestParam(value = "images", required = false) List<MultipartFile> files,
            @RequestParam(value = "variants", required = false) String variantsJson) {
        try {
            // Lấy Product entity trực tiếp từ repository
            java.util.Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ResponseData("Product not found", 404, 404, null));
            }
            Product product = productOpt.get();
            List<String> oldImages = product.getImages();

            List<String> imageNames = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.badRequest()
                                .body(new ResponseData("Only image files are allowed", 400, 400, null));
                    }
                    String fileName = fileStorageService.storeFile(file);
                    imageNames.add(fileName);
                }
                // Xóa ảnh cũ
                if (oldImages != null) {
                    for (String oldImage : oldImages) {
                        try {
                            fileStorageService.deleteFile(oldImage);
                        } catch (IOException e) {
                            System.err.println("Failed to delete old image: " + oldImage);
                        }
                    }
                }
            } else {
                // Nếu không upload ảnh mới, giữ lại ảnh cũ
                imageNames = oldImages;
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
            productRequest.setStock(stock);
            productRequest.setImages(imageNames);
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
                // Xóa các file ảnh
                if (images != null) {
                    for (String image : images) {
                        try {
                            fileStorageService.deleteFile(image);
                        } catch (IOException e) {
                            System.err.println("Failed to delete image: " + image);
                        }
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
