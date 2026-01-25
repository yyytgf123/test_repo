package com.groom.common.event;

import java.util.UUID;

public record PaymentCompletedEvent(
    UUID orderId,
    String paymentKey,
    Long amount
) {
    public static PaymentCompletedEvent of(UUID orderId, String paymentKey, Long amount) {
        return new PaymentCompletedEvent(orderId, paymentKey, amount);
    }
}
