package com.gl.paymentservice;

import com.gl.paymentservice.client.OrderServiceClient;
import com.gl.paymentservice.client.RewardServiceClient;
import com.gl.paymentservice.client.UserServiceClient;
import com.gl.paymentservice.dto.*;
import com.gl.paymentservice.entity.Payment;
import com.gl.paymentservice.entity.Wallet;
import com.gl.paymentservice.enums.PaymentMethod;
import com.gl.paymentservice.enums.PaymentStatus;
import com.gl.paymentservice.repository.PaymentRepository;
import com.gl.paymentservice.repository.WalletRepository;
import com.gl.paymentservice.service.impl.PaymentServiceImpl;
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
class PaymentServiceImplTest {
    @Mock private PaymentRepository paymentRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private OrderServiceClient orderServiceClient;
    @Mock private RewardServiceClient rewardServiceClient;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Test
    void topUpWallet_ShouldIncreaseBalance() {
        // 1. Mock the user profile retrieval
        when(userServiceClient.getCurrentUserProfile())
                .thenReturn(UserResponseDto.builder().id(1L).build());

        // 2. Mock the current wallet state (starting with 100)
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(
                Wallet.builder()
                        .id(1L)
                        .userId(1L)
                        .balance(BigDecimal.valueOf(100))
                        .lastUpdated(LocalDateTime.now())
                        .build()
        ));

        // 3. Mock the save operation to return the object passed to it
        when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

        // 4. Execute the top-up (adding 200)
        WalletResponseDto response = paymentService.topUpWallet(
                WalletTopUpRequestDto.builder().amount(BigDecimal.valueOf(200)).build()
        );

        // 5. Verify the math (100 + 200 = 300)
        assertEquals(BigDecimal.valueOf(300), response.getBalance());
        verify(walletRepository).save(any(Wallet.class));
    }
    @Test
    void processPayment_ShouldDeductWalletAndMarkPaid() {
        // 1. Mock User Profile
        when(userServiceClient.getCurrentUserProfile())
                .thenReturn(UserResponseDto.builder().id(1L).build());

        // 2. Mock Order details (Total: 250)
        when(orderServiceClient.getOrderById(1L)).thenReturn(
                OrderResponseDto.builder()
                        .id(1L)
                        .userId(1L)
                        .totalAmount(BigDecimal.valueOf(250))
                        .status("PENDING")
                        .build()
        );

        // 3. Mock Wallet state (Balance: 300)
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(
                Wallet.builder()
                        .id(1L)
                        .userId(1L)
                        .balance(BigDecimal.valueOf(300))
                        .lastUpdated(LocalDateTime.now())
                        .build()
        ));

        // 4. Mock Wallet save
        when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

        // 5. Mock Payment save (Simulate ID generation)
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment payment = inv.getArgument(0);
            payment.setId(10L);
            return payment;
        });

        // 6. Execute
        PaymentResponseDto response = paymentService.processPayment(
                PaymentRequestDto.builder()
                        .orderId(1L)
                        .method(PaymentMethod.UPI)
                        .useWallet(true)
                        .rewardPointsToRedeem(0)
                        .build()
        );

        // 7. Assertions
        assertEquals(10L, response.getId());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        // Verify dependencies were called
        verify(walletRepository).save(any(Wallet.class));
        verify(orderServiceClient).updateOrderStatus(eq(1L), any(OrderStatusUpdateRequestDto.class));
    }
    @Test
    void getMyPayments_ShouldReturnList() {
        // 1. Mock the user profile retrieval
        when(userServiceClient.getCurrentUserProfile())
                .thenReturn(UserResponseDto.builder().id(1L).build());

        // 2. Mock the repository to return a list containing one payment
        when(paymentRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(
                Payment.builder()
                        .id(1L)
                        .orderId(1L)
                        .userId(1L)
                        .originalAmount(BigDecimal.TEN)
                        .walletUsed(BigDecimal.ZERO)
                        .rewardPointsUsed(0)
                        .finalAmountPaid(BigDecimal.TEN)
                        .method(PaymentMethod.UPI)
                        .status(PaymentStatus.SUCCESS)
                        .transactionRef("TXN-123")
                        .createdAt(LocalDateTime.now())
                        .build()
        ));

        // 3. Execute the service method
        List<PaymentResponseDto> result = paymentService.getMyPayments();

        // 4. Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TXN-123", result.get(0).getTransactionRef());
    }
}