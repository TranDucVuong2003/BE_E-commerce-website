package com.tranvuong.be_e_commerce.Services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tranvuong.be_e_commerce.Entity.Category;
import com.tranvuong.be_e_commerce.Repository.CategoryRepository;
import com.tranvuong.be_e_commerce.Services.CategoryService;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;
import java.util.NoSuchElementException;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ResponseData getAllCategories() {
        return new ResponseData("Success", 200, 200, categoryRepository.findAll());
    }

    @Override
    public ResponseData getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + id));
        return new ResponseData("Success", 200, 200, category);
    }

    @Override
    public ResponseData createCategory(Category category){
        Category createdCategory = categoryRepository.save(category);
        return new ResponseData("Success", 200, 200,createdCategory);

    }

    @Override
    public ResponseData updateCategory(Long id, Category category) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + id));
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setCreated_at(category.getCreated_at());
        return new ResponseData("Update Success", 200, 200, categoryRepository.save(existing));
    }
  
    @Override
    public ResponseData deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + id));
        categoryRepository.delete(category);
        return new ResponseData("Delete success!", 200, 200, null);
    }
    // public void deleteCategory(Long id);
}
