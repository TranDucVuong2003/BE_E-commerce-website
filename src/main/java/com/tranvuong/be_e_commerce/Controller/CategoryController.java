package com.tranvuong.be_e_commerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // Lấy tất cả categories
    @GetMapping
    public ResponseEntity<ResponseData> getAllCategores() {
        ResponseData categories = categoryService.getAllCategories();
        return ResponseEntity.status(categories.getStatusCode()).body(categories);
    }

    // Tạo mới category
    @PostMapping
    public ResponseEntity<ResponseData> createCategory(@RequestBody Category category) {
        ResponseData createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(createdCategory.getStatusCode()).body(createdCategory);
    }

    // Lấy theo id
    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseData> getCategoryById(@PathVariable String id) {
        ResponseData categories = categoryService.getCategoryById(id);
        return ResponseEntity.status(categories.getStatusCode()).body(categories);
    }

    // Xóa category theo id
    @DeleteMapping("/id/{id}")
    public ResponseEntity<ResponseData> deleteCategoryById(@PathVariable String id){
        try {
            // Lấy thông tin category trước khi xáo
            ResponseData currentCategory = categoryService.getCategoryById(id);
            if (currentCategory.getStatusCode() == 404) {
                return ResponseEntity.status(404)
                        .body(new ResponseData("Category not found", 404, 404, null));
            }
            
            // Xóa category
            ResponseData response = categoryService.deleteCategory(id);

            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.badRequest()
            .body(new ResponseData(e.getMessage(), 400, 400, null));
        }
    }

    //Update category
    @PutMapping("/id/{id}")
    public ResponseEntity<ResponseData> updateCategory(@PathVariable String id, @RequestBody Category category) {
        try {
            ResponseData response = categoryService.updateCategory(id, category);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ResponseData("Failed to update category: " + e.getMessage(), 400, 400, null));
        }
    }
}
