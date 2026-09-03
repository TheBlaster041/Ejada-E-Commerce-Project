package com.example.inventory.controller;

import com.example.inventory.entity.Category;
import com.example.inventory.repository.CategoryRepository;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/categories")
public class CategoryController {

    private final CategoryRepository categories;

    public CategoryController(CategoryRepository categories) {
        this.categories = categories;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {

        String name = body.get("name");

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "category name is required"));
        }

        if (categories.findByName(name).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "category already exists"));
        }

        Category category = categories.save(new Category(name));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(category);
    }

    @GetMapping
    public List<Category> all() {
        return categories.findAll();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> get(@PathVariable Long categoryId) {

        return categories.findById(categoryId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                    ResponseEntity.notFound().build()
                );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> delete(@PathVariable Long categoryId) {

        if (!categories.existsById(categoryId)) {
            return ResponseEntity.notFound().build();
        }

        categories.deleteById(categoryId);

        return ResponseEntity.ok(
                Map.of("message", "category deleted")
        );
    }
}