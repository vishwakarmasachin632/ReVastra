package com.gl.paymentservice.controller;

import com.gl.paymentservice.dto.*;
import com.gl.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/wallet")
    public ResponseEntity<WalletResponseDto> getMyWallet() {
        return ResponseEntity.ok(paymentService.getMyWallet());
    }

    @PostMapping("/wallet/topup")
    public ResponseEntity<WalletResponseDto> topUpWallet(@Valid @RequestBody WalletTopUpRequestDto request) {
        return ResponseEntity.ok(paymentService.topUpWallet(request));
    }

    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentResponseDto>> getMyPayments() {
        return ResponseEntity.ok(paymentService.getMyPayments());
    }

    @GetMapping("/admin/summary")
    public ResponseEntity<PaymentSummaryDto> getPaymentSummary() {
        return ResponseEntity.ok(paymentService.getPaymentSummary());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}