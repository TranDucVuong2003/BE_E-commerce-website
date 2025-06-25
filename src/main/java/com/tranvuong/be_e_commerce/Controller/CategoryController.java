package com.tranvuong.be_e_commerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tranvuong.be_e_commerce.Entity.Category;
import com.tranvuong.be_e_commerce.Services.Impl.CategoryServiceImpl;
import com.tranvuong.be_e_commerce.dto.response.ResponseData;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;
    
    //Lấy tất cả categories
    @GetMapping
    public ResponseEntity<ResponseData> getAllCategores(){
        ResponseData categories = categoryService.getAllCategories();
        return ResponseEntity.status(categories.getStatusCode()).body(categories);
    }

    // Tạo mới category
    @PostMapping
    public ResponseEntity<ResponseData> createCategory(@RequestBody Category category ){
        ResponseData createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(createdCategory.getStatusCode()).body(createdCategory);

    }


}
