package com.gl.paymentservice.dto;

import com.gl.paymentservice.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    private Boolean useWallet;

    @Min(value = 0, message = "Reward points cannot be negative")
    private Integer rewardPointsToRedeem;
}