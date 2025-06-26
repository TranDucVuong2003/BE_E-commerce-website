package com.tranvuong.be_e_commerce.Services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tranvuong.be_e_commerce.Entity.Category;
import com.tranvuong.be_e_commerce.Repository.CategoryRepository;
import com.tranvuong.be_e_commerce.Services.CategoryService;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ResponseData getAllCategories() {
        return new ResponseData("Success", 200, 200, categoryRepository.findAll());
    }

    @Override
    public ResponseData getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + id));
        return new ResponseData("Success", 200, 200, category);
    }

    @Override
    public ResponseData createCategory(Category category){
        try {
            Category newCategory = new Category();
            
            newCategory.setName(category.getName());
            newCategory.setDescription(category.getDescription());
            newCategory.setCreated_at(LocalDate.now());
            
            Category createdCategory = categoryRepository.save(newCategory);
            return new ResponseData("Success", 200, 200,createdCategory);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseData("Failed to create category", 400, 400, null);

        }

    }

    @Override
    public ResponseData updateCategory(String id, Category category) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + id));
        if (category.getName() != null) {
            existing.setName(category.getName());
        }
        if (category.getDescription() != null) {
            existing.setDescription(category.getDescription());
        }
        if (category.getCreated_at() != null) {
            existing.setCreated_at(category.getCreated_at());
        }
        return new ResponseData("Update Success", 200, 200, categoryRepository.save(existing));
    }
  
    @Override
    public ResponseData deleteCategory(String id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isPresent()) {
                categoryRepository.deleteById(id);
                return new ResponseData("Category deleted successfully", 200, 200, null);
            } else {
                return new ResponseData("Category not found", 404, 404, null);
            }
        } catch (Exception e) {
            return new ResponseData("Failed to delete Category", 400, 400, null);
        }
    }
    // public void deleteCategory(Long id);
}
