package com.gl.upcycleservice;

import com.gl.upcycleservice.client.OrderServiceClient;
import com.gl.upcycleservice.client.UserServiceClient;
import com.gl.upcycleservice.dto.*;
import com.gl.upcycleservice.entity.UpcycleProduct;
import com.gl.upcycleservice.entity.WorkerProfile;
import com.gl.upcycleservice.enums.ProductStatus;
import com.gl.upcycleservice.repository.UpcycleProductRepository;
import com.gl.upcycleservice.repository.WorkerProfileRepository;
import com.gl.upcycleservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private UpcycleProductRepository upcycleProductRepository;
    @Mock private WorkerProfileRepository workerProfileRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private OrderServiceClient orderServiceClient;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void addProduct_ShouldAdd_WhenWorkerVerified() {
        // Mock User Profile
        when(userServiceClient.getCurrentUserProfile())
                .thenReturn(UserResponseDto.builder().id(2L).build());

        // Mock Worker Profile (Verified)
        when(workerProfileRepository.findByUserId(2L)).thenReturn(Optional.of(
                WorkerProfile.builder()
                        .id(1L)
                        .userId(2L)
                        .workerName("Worker One")
                        .verifiedBadge(true)
                        .build()
        ));

        // Mock Repository Save
        when(upcycleProductRepository.save(any(UpcycleProduct.class))).thenAnswer(inv -> {
            UpcycleProduct p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        // Execute
        ProductResponseDto response = productService.addProduct(
                ProductRequestDto.builder()
                        .title("Bag")
                        .description("Denim bag")
                        .price(BigDecimal.valueOf(499))
                        .build()
        );

        // Assert
        assertEquals(10L, response.getId());
        assertEquals(ProductStatus.AVAILABLE, response.getStatus());
    }

    @Test
    void getAvailableProducts_ShouldReturnList() {
        // Mock Repository query
        when(upcycleProductRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.AVAILABLE))
                .thenReturn(List.of(
                        UpcycleProduct.builder()
                                .id(1L)
                                .title("Bag")
                                .description("Desc")
                                .price(BigDecimal.TEN)
                                .status(ProductStatus.AVAILABLE)
                                .workerName("Worker One")
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        List<ProductResponseDto> result = productService.getAvailableProducts();

        assertEquals(1, result.size());
        assertEquals("Bag", result.get(0).getTitle());
    }

    @Test
    void purchaseProduct_ShouldCreateOrderAndMarkSold() {
        // Mock User Profile
        when(userServiceClient.getCurrentUserProfile())
                .thenReturn(UserResponseDto.builder().id(1L).build());

        // Mock Product to be purchased
        when(upcycleProductRepository.findById(1L)).thenReturn(Optional.of(
                UpcycleProduct.builder()
                        .id(1L)
                        .workerProfileId(5L)
                        .price(BigDecimal.valueOf(499))
                        .status(ProductStatus.AVAILABLE)
                        .build()
        ));

        // Mock Worker Profile lookup
        when(workerProfileRepository.findById(5L)).thenReturn(Optional.of(
                WorkerProfile.builder().id(5L).userId(2L).workerName("Worker One").build()
        ));

        // Mock Order creation
        when(orderServiceClient.createOrder(any(OrderRequestDto.class))).thenReturn(
                OrderResponseDto.builder().id(100L).userId(1L).workerId(2L).build()
        );

        // Mock Product save (marking as sold)
        when(upcycleProductRepository.save(any(UpcycleProduct.class))).thenAnswer(inv -> inv.getArgument(0));

        // Execute
        OrderResponseDto response = productService.purchaseProduct(1L);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        verify(upcycleProductRepository).save(argThat(product -> product.getStatus() == ProductStatus.SOLD));
    }
}