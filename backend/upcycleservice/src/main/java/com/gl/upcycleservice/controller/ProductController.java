package com.gl.upcycleservice.controller;

import com.gl.upcycleservice.dto.OrderResponseDto;
import com.gl.upcycleservice.dto.ProductRequestDto;
import com.gl.upcycleservice.dto.ProductResponseDto;
import com.gl.upcycleservice.dto.PurchaseRequestDto;
import com.gl.upcycleservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/upcycle")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> addProduct(@Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity.ok(productService.addProduct(request));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping("/purchase")
    public ResponseEntity<OrderResponseDto> purchaseProduct(@Valid @RequestBody PurchaseRequestDto request) {
        return ResponseEntity.ok(productService.purchaseProduct(request.getProductId()));
    }
}