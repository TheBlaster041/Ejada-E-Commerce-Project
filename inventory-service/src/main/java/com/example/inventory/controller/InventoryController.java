package com.example.inventory.controller;

import com.example.inventory.entity.Category;
import com.example.inventory.entity.Product;
import com.example.inventory.repository.CategoryRepository;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.service.ImageStorageService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final ProductRepository repo;
    private final ImageStorageService imageStorage;
    private final CategoryRepository categories;
    public InventoryController(ProductRepository repo, ImageStorageService imageStorage,CategoryRepository categories)
    {
        this.repo = repo;
        this.imageStorage = imageStorage;
        this.categories = categories;
    }

    @PostMapping(value="/products", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam String sku,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam(defaultValue="0") Integer quantity,
            @RequestParam Long categoryId,
            @RequestParam("image") MultipartFile image) {

        if (repo.findBySku(sku).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "SKU already exists"));
        }

        if (price.signum() < 0 || quantity < 0) 
        {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "price and quantity cannot be negative"));
        }
        
        Category category = categories.findById(categoryId)
                .orElse(null);

        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "category not found"));
        }
        Product p = new Product();
        p.setSku(sku);
        p.setName(name);
        p.setPrice(price);
        p.setQuantity(quantity);
        p.setCategory(category);
        p.setImagePath(imageStorage.store(image));

        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(p));
    }

    @GetMapping("/products")
    public List<Product> all() {
        return repo.findAll();
    }

    @GetMapping("/products/{sku}")
    public ResponseEntity<?> get(@PathVariable String sku) {
        return repo.findBySku(sku)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value="/products/{sku}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable String sku,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam Integer quantity,
            @RequestParam(value="image", required=false) MultipartFile image) {

        return repo.findBySku(sku).map(p -> {
            p.setName(name);
            p.setPrice(price);
            p.setQuantity(quantity);

            if (image != null && !image.isEmpty()) {
                String oldPath = p.getImagePath();
                p.setImagePath(imageStorage.store(image));
                imageStorage.delete(oldPath);
            }

            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/products/{sku}")
    public ResponseEntity<?> delete(@PathVariable String sku) {
        return repo.findBySku(sku).map(p -> {
            imageStorage.delete(p.getImagePath());
            repo.delete(p);
            return ResponseEntity.ok(Map.of("message", "deleted"));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/products/{sku}/image")
    public ResponseEntity<?> image(@PathVariable String sku) {
        Product product = repo.findBySku(sku).orElse(null);
        if (product == null || product.getImagePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Path.of(product.getImagePath()).toAbsolutePath().normalize();

            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(path);
            MediaType mediaType = contentType == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(contentType);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(Files.readAllBytes(path));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Could not load image"));
        }
    }

    @PostMapping("/stock/{sku}/add")
    public ResponseEntity<?> add(@PathVariable String sku, @RequestParam int quantity) {
        if (quantity <= 0)
            return ResponseEntity.badRequest().body(Map.of("message","quantity must be positive"));

        return repo.findBySku(sku).map(p -> {
            p.setQuantity(p.getQuantity() + quantity);
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/stock/{sku}/remove")
    public ResponseEntity<?> remove(@PathVariable String sku, @RequestParam int quantity) {
        if (quantity <= 0)
            return ResponseEntity.badRequest().body(Map.of("message","quantity must be positive"));

        return repo.findBySku(sku).map(p -> {
            if (p.getQuantity() < quantity)
                return ResponseEntity.badRequest().body(Map.of("message","insufficient stock"));

            p.setQuantity(p.getQuantity() - quantity);
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stock/{sku}/available")
    public ResponseEntity<?> available(@PathVariable String sku, @RequestParam int quantity) {
        return repo.findBySku(sku)
                .map(p -> ResponseEntity.ok(Map.of(
                        "sku", sku,
                        "requested", quantity,
                        "available", p.getQuantity() >= quantity,
                        "quantity", p.getQuantity())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
