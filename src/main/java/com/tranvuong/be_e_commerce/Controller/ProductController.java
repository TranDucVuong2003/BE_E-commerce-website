package com.tranvuong.be_e_commerce.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tranvuong.be_e_commerce.Entity.Product;
import com.tranvuong.be_e_commerce.Services.FileStorageService;
import com.tranvuong.be_e_commerce.Services.Impl.ProductServiceImp;
import com.tranvuong.be_e_commerce.dto.request.ProductRequest;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductServiceImp productService;

    @Autowired
    private FileStorageService fileStorageService;

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
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("category_id") String categoryId,
            @RequestParam("size") String size,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Double quantity,
            @RequestParam("stock") Boolean stock,
            @RequestParam(value = "images", required = false) List<MultipartFile> files // đổi "image" thành "images"
    ) {
        try {
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

            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(name);
            productRequest.setDescription(description);
            productRequest.setCategory_id(categoryId);
            productRequest.setSize(size);
            productRequest.setPrice(price);
            productRequest.setQuantity(quantity);
            productRequest.setStock(stock);
            productRequest.setImages(imageNames);

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
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("category_id") String categoryId,
            @RequestParam("size") String size,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Double quantity,
            @RequestParam("stock") Boolean stock,
            @RequestParam(value = "images", required = false) List<MultipartFile> files
    ) {
        try {
            // Lấy sản phẩm hiện tại để xóa ảnh cũ
            ResponseData currentProduct = productService.getProductById(id);
            if (currentProduct.getStatusCode() == 404) {
                return ResponseEntity.status(404)
                        .body(new ResponseData("Product not found", 404, 404, null));
            }

            Product product = (Product) currentProduct.getData();
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

            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(name);
            productRequest.setDescription(description);
            productRequest.setCategory_id(categoryId);
            productRequest.setSize(size);
            productRequest.setPrice(price);
            productRequest.setQuantity(quantity);
            productRequest.setStock(stock);
            productRequest.setImages(imageNames);

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
            // Lấy thông tin sản phẩm trước khi xóa để xóa ảnh
            ResponseData currentProduct = productService.getProductById(id);
            if (currentProduct.getStatusCode() == 404) {
                return ResponseEntity.status(404)
                        .body(new ResponseData("Product not found", 404, 404, null));
            }

            Product product = (Product) currentProduct.getData();
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
