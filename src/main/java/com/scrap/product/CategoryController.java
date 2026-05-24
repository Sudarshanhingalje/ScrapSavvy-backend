package com.scrap.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.scrap.product.entity.Category;
import com.scrap.product.repository.CategoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/getcategories")
    public ResponseEntity<List<Category>> viewListedScrap() {
        List<Category> catList = categoryRepository.findAll();
        return new ResponseEntity<>(catList, HttpStatus.OK);
    }
}
