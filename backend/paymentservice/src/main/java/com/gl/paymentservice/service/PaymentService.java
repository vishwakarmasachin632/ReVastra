package com.gl.paymentservice.service;

import com.gl.paymentservice.dto.*;

import java.util.List;

public interface PaymentService {

    PaymentResponseDto processPayment(PaymentRequestDto request);

    WalletResponseDto getMyWallet();

    WalletResponseDto topUpWallet(WalletTopUpRequestDto request);

    List<PaymentResponseDto> getMyPayments();

    PaymentSummaryDto getPaymentSummary();
    List<PaymentResponseDto> getAllPayments();
}