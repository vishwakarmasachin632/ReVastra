package com.gl.paymentservice.service.impl;

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
import com.gl.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final UserServiceClient userServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final RewardServiceClient rewardServiceClient;

    @Override
    public PaymentResponseDto processPayment(PaymentRequestDto request) {

        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();
        OrderResponseDto order = orderServiceClient.getOrderById(request.getOrderId());

        if (!order.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to pay for this order");
        }

        if (request.getMethod() != PaymentMethod.CASH) {
            throw new RuntimeException("Only CASH payment is enabled right now");
        }

        BigDecimal originalAmount = order.getTotalAmount();
        BigDecimal remainingAmount = originalAmount;
        BigDecimal walletUsed = BigDecimal.ZERO;
        int rewardPointsUsed = 0;

        Wallet wallet = getOrCreateWallet(currentUser.getId());

        if (request.getRewardPointsToRedeem() != null && request.getRewardPointsToRedeem() > 0) {
            rewardServiceClient.redeemPoints(
                    RewardRedeemRequestDto.builder()
                            .userId(currentUser.getId())
                            .pointsToRedeem(request.getRewardPointsToRedeem())
                            .note("Redeemed during payment")
                            .build()
            );

            rewardPointsUsed = request.getRewardPointsToRedeem();

            BigDecimal discount = BigDecimal.valueOf(rewardPointsUsed)
                    .divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP);

            remainingAmount = remainingAmount.subtract(discount);
            if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
                remainingAmount = BigDecimal.ZERO;
            }
        }

        if (Boolean.TRUE.equals(request.getUseWallet())) {
            if (wallet.getBalance().compareTo(remainingAmount) >= 0) {
                walletUsed = remainingAmount;
                wallet.setBalance(wallet.getBalance().subtract(walletUsed));
                remainingAmount = BigDecimal.ZERO;
            } else {
                walletUsed = wallet.getBalance();
                remainingAmount = remainingAmount.subtract(walletUsed);
                wallet.setBalance(BigDecimal.ZERO);
            }
            wallet.setLastUpdated(LocalDateTime.now());
            walletRepository.save(wallet);
        }

        PaymentMethod finalMethod = request.getMethod();
        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0 && Boolean.TRUE.equals(request.getUseWallet())) {
            finalMethod = PaymentMethod.WALLET;
        }

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .userId(currentUser.getId())
                .originalAmount(originalAmount)
                .walletUsed(walletUsed)
                .rewardPointsUsed(rewardPointsUsed)
                .finalAmountPaid(remainingAmount)
                .method(finalMethod)
                .status(PaymentStatus.SUCCESS)
                .transactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .createdAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        orderServiceClient.updateOrderStatus(
                savedPayment.getOrderId(),
                OrderStatusUpdateRequestDto.builder()
                        .status("PAID")
                        .paymentStatus("SUCCESS")
                        .paymentTime(savedPayment.getCreatedAt())
                        .build()
        );

        return mapToDto(savedPayment);
    }

    @Override
    public WalletResponseDto getMyWallet() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();
        Wallet wallet = getOrCreateWallet(currentUser.getId());
        return mapWalletToDto(wallet);
    }

    @Override
    public WalletResponseDto topUpWallet(WalletTopUpRequestDto request) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();
        Wallet wallet = getOrCreateWallet(currentUser.getId());

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setLastUpdated(LocalDateTime.now());

        Wallet savedWallet = walletRepository.save(wallet);
        return mapWalletToDto(savedWallet);
    }

    @Override
    public List<PaymentResponseDto> getMyPayments() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(
                        Wallet.builder()
                                .userId(userId)
                                .balance(BigDecimal.ZERO)
                                .lastUpdated(LocalDateTime.now())
                                .build()
                ));
    }

    @Override
    public PaymentSummaryDto getPaymentSummary() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Only admin can access payment summary");
        }

        BigDecimal totalEarnings = paymentRepository.getTotalSuccessfulEarnings();

        return PaymentSummaryDto.builder()
                .totalPayments(paymentRepository.count())
                .totalEarnings(totalEarnings)
                .build();
    }

    @Override
    public List<PaymentResponseDto> getAllPayments() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Only admin can access all payments");
        }

        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private PaymentResponseDto mapToDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .originalAmount(payment.getOriginalAmount())
                .walletUsed(payment.getWalletUsed())
                .rewardPointsUsed(payment.getRewardPointsUsed())
                .finalAmountPaid(payment.getFinalAmountPaid())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionRef(payment.getTransactionRef())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private WalletResponseDto mapWalletToDto(Wallet wallet) {
        return WalletResponseDto.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .lastUpdated(wallet.getLastUpdated())
                .build();
    }
}