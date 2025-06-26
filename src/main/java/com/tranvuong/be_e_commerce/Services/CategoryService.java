package com.tranvuong.be_e_commerce.Services;


import com.tranvuong.be_e_commerce.Entity.Category;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;


public interface CategoryService {
    //Lấy toàn bộ Category
    public ResponseData getAllCategories();

    public ResponseData getCategoryById(String id);

    public ResponseData createCategory(Category category);

    public ResponseData updateCategory(String id, Category category);
    
    public ResponseData deleteCategory(String id);
}
