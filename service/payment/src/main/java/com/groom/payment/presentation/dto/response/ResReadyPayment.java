package com.groom.payment.presentation.dto.response;

import java.util.UUID;

public record ResReadyPayment(
	UUID orderId,
	Long amount,
	String clientKey,
	String successUrl,
	String failUrl
) {}
