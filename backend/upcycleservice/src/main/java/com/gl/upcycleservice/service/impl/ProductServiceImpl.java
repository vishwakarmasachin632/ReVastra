package com.gl.upcycleservice.service.impl;

import com.gl.upcycleservice.client.OrderServiceClient;
import com.gl.upcycleservice.client.UserServiceClient;
import com.gl.upcycleservice.dto.*;
import com.gl.upcycleservice.entity.UpcycleProduct;
import com.gl.upcycleservice.entity.WorkerProfile;
import com.gl.upcycleservice.enums.ProductStatus;
import com.gl.upcycleservice.enums.ServiceType;
import com.gl.upcycleservice.exception.ResourceNotFoundException;
import com.gl.upcycleservice.exception.UnauthorizedException;
import com.gl.upcycleservice.repository.UpcycleProductRepository;
import com.gl.upcycleservice.repository.WorkerProfileRepository;
import com.gl.upcycleservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final UpcycleProductRepository upcycleProductRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final UserServiceClient userServiceClient;
    private final OrderServiceClient orderServiceClient;

    @Override
    public ProductResponseDto addProduct(ProductRequestDto request) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        WorkerProfile profile = workerProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Worker profile not found for current user"));

        if (!Boolean.TRUE.equals(profile.getVerifiedBadge())) {
            throw new UnauthorizedException("Only verified workers can add upcycle products");
        }

        UpcycleProduct product = UpcycleProduct.builder()
                .workerProfileId(profile.getId())
                .workerName(profile.getWorkerName())
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(ProductStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();

        UpcycleProduct saved = upcycleProductRepository.save(product);
        return mapToDto(saved);
    }

    @Override
    public List<ProductResponseDto> getAvailableProducts() {
        return upcycleProductRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.AVAILABLE)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ProductResponseDto getProductById(Long productId) {
        UpcycleProduct product = upcycleProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return mapToDto(product);
    }

    @Override
    public OrderResponseDto purchaseProduct(Long productId) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        UpcycleProduct product = upcycleProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new RuntimeException("Product is not available for purchase");
        }

        WorkerProfile workerProfile = workerProfileRepository.findById(product.getWorkerProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Worker profile not found"));

        OrderRequestDto orderRequest = OrderRequestDto.builder()
                .serviceType(ServiceType.UPCYCLE)
                .workerId(workerProfile.getUserId())
                .totalAmount(product.getPrice())
                .build();

        OrderResponseDto orderResponse = orderServiceClient.createOrder(orderRequest);

        product.setStatus(ProductStatus.SOLD);
        upcycleProductRepository.save(product);

        return orderResponse;
    }

    private ProductResponseDto mapToDto(UpcycleProduct product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .workerProfileId(product.getWorkerProfileId())
                .workerName(product.getWorkerName())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .build();
    }
}