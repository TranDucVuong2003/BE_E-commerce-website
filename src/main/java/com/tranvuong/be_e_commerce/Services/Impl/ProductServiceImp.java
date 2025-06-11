package com.tranvuong.be_e_commerce.Services.Impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tranvuong.be_e_commerce.Entity.Product;
import com.tranvuong.be_e_commerce.Repository.ProductRepository;
import com.tranvuong.be_e_commerce.Services.ProductService;
import com.tranvuong.be_e_commerce.dto.request.ProductRequest;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductRepository productRepository;



    @Override
    public ResponseData getAllProducts() {
        return new ResponseData("Success", 200, 200, productRepository.findAll());
    }

    @Override
    @Transactional
    public ResponseData createdProduct(ProductRequest productRequest) {
        try {
            Product product = new Product();

            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setCategory_id(productRequest.getCategory_id());
            product.setSize(productRequest.getSize());
            product.setPrice(productRequest.getPrice());
            product.setQuantity(productRequest.getQuantity());
            product.setStock(productRequest.isStock());
            product.setImages(productRequest.getImages());
            product.setCreated_at(LocalDate.now());

            Product savedProduct = productRepository.save(product);

            return new ResponseData("Product created successfully", 200, 200, savedProduct);
        } catch (Exception e) {
            return new ResponseData("Failed to create product", 400, 400, null);
        }
    }

    @Override
    @Transactional
    public ResponseData updateProduct(ProductRequest productRequest, String id ){
        try {
             
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                Product product1 = product.get();
                // xử lý với product
                product1.setName(productRequest.getName());
                product1.setDescription(productRequest.getDescription());
                product1.setCategory_id(productRequest.getCategory_id());
                product1.setSize(productRequest.getSize());
                product1.setPrice(productRequest.getPrice());
                product1.setQuantity(productRequest.getQuantity());
                product1.setStock(productRequest.isStock());
                product1.setImages(productRequest.getImages());
                product1.setCreated_at(LocalDate.now());
    
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
    public ResponseData getProductById(String id){
        try {
            Optional<Product> product = productRepository.findById(id);
            if(product.isPresent()){
                return new ResponseData("Product found successfully", 200, 200, product.get());
            }else{
                return new ResponseData("Product not found", 404, 404, null);
            }
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseData("Error occurred while fetching product", 500, 500, null);
        }
    }

}
