package com.tranvuong.be_e_commerce.Services;

import org.springframework.stereotype.Service;

import com.tranvuong.be_e_commerce.dto.request.ProductRequest;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

@Service
public interface ProductService {
    //Lấy tất cả sản phẩm 
    public ResponseData getAllProducts();

    //Tạo sản phẩm mới
    public ResponseData createdProduct(ProductRequest productRequest);

    // Cập nhật sản phẩm
    public ResponseData updateProduct(ProductRequest productRequest, String id);

    // Xóa sản phẩm
    public ResponseData deleteProduct(String id);

    //Lấy sản phẩm theo id
    public ResponseData getProductById(String id);
}
