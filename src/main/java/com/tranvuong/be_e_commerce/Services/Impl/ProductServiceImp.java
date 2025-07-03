package com.tranvuong.be_e_commerce.Services.Impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tranvuong.be_e_commerce.Entity.Product;
import com.tranvuong.be_e_commerce.Entity.ProductVariant;
import com.tranvuong.be_e_commerce.Repository.ProductRepository;
import com.tranvuong.be_e_commerce.Repository.ProductVariantRepository;
import com.tranvuong.be_e_commerce.Services.ProductService;
import com.tranvuong.be_e_commerce.dto.request.ProductRequest;
import com.tranvuong.be_e_commerce.dto.request.ProductVariantDto;
import com.tranvuong.be_e_commerce.dto.response.ProductResponse;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Override
    public ResponseData getAllProducts() {
        return new ResponseData("Success", 200, 200, productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public ResponseData createdProduct(ProductRequest productRequest) {
        try {
            Product product = new Product();

            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setCategory_id(productRequest.getCategory_id());
            product.setPrice(productRequest.getPrice());
            product.setStock(productRequest.isStock());
            product.setImages(productRequest.getImages());
            product.setCreated_at(LocalDate.now());

            // Map từ DTO sang entity
            List<ProductVariant> variants = productRequest.getProductVariants().stream().map(variantDto -> {
                ProductVariant variant = new ProductVariant();
                variant.setSize(variantDto.getSize());
                variant.setQuantity(variantDto.getQuantity());
                variant.setProduct(product); // thiết lập quan hệ ngược
                return variant;
            }).collect(Collectors.toList());

            product.setProductVariants(variants);

            Product savedProduct = productRepository.save(product);

            return new ResponseData("Product created successfully", 200, 200, savedProduct);
        } catch (Exception e) {
            return new ResponseData("Failed to create product", 400, 400, null);
        }
    }

    @Override
    @Transactional
    public ResponseData updateProduct(ProductRequest productRequest, String id) {
        try {

            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                Product product1 = product.get();
                // xử lý với product
                if (productRequest.getName() != null) {
                    product1.setName(productRequest.getName());
                }
                if (productRequest.getDescription() != null) {
                    product1.setDescription(productRequest.getDescription());
                }
                if (productRequest.getCategory_id() != null) {
                    product1.setCategory_id(productRequest.getCategory_id());
                }
                if (productRequest.getPrice() != null) {
                    product1.setPrice(productRequest.getPrice());
                }
                if (productRequest.getImages() != null) {
                    product1.setImages(productRequest.getImages());
                }
                if (productRequest.getProductVariants() != null) {
                    List<ProductVariant> variants = product1.getProductVariants();
                    variants.clear();
                    List<ProductVariant> newVariants = productRequest.getProductVariants().stream().map(variantDto -> {
                        ProductVariant variant = new ProductVariant();
                        variant.setSize(variantDto.getSize());
                        variant.setQuantity(variantDto.getQuantity());
                        variant.setProduct(product1);
                        return variant;
                    }).collect(Collectors.toList());
                    variants.addAll(newVariants);
                }

                Product savedProduct = productRepository.save(product1);

                return new ResponseData("Product update successfully", 200, 200, savedProduct);
            } else {
                // xử lý trường hợp không tìm thấy
                return new ResponseData("Failed to update product", 400, 400, null);
            }

        } catch (Exception e) {
            return new ResponseData("Failed to update product", 400, 400, null);
        }
    }

    @Override
    @Transactional
    public ResponseData deleteProduct(String id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                productRepository.deleteById(id);
                return new ResponseData("Product deleted successfully", 200, 200, null);
            } else {
                return new ResponseData("Product not found", 404, 404, null);
            }
        } catch (Exception e) {
            return new ResponseData("Failed to delete product", 400, 400, null);
        }
    }

    @Override
    public ResponseData getProductById(String id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                return new ResponseData("Product found successfully", 200, 200, mapToProductResponse(product.get()));
            } else {
                return new ResponseData("Product not found", 404, 404, null);
            }
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseData("Error occurred while fetching product", 500, 500, null);
        }
    }

    // Mapping từ Entity sang DTO khi trả về
    public ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setCategoryId(product.getCategory_id());
        response.setPrice(product.getPrice());
        response.setStock(product.isStock());
        response.setImages(product.getImages());

        List<ProductVariantDto> variantDtos = product.getProductVariants().stream().map(variant -> {
            ProductVariantDto dto = new ProductVariantDto();
            dto.setSize(variant.getSize());
            dto.setQuantity(variant.getQuantity());
            return dto;
        }).collect(Collectors.toList());

        response.setProductVariants(variantDtos);
        return response;
    }

}
