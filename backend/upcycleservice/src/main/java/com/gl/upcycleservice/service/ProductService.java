package com.gl.upcycleservice.service;

import com.gl.upcycleservice.dto.OrderResponseDto;
import com.gl.upcycleservice.dto.ProductRequestDto;
import com.gl.upcycleservice.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto addProduct(ProductRequestDto request);

    List<ProductResponseDto> getAvailableProducts();

    ProductResponseDto getProductById(Long productId);

    OrderResponseDto purchaseProduct(Long productId);
}