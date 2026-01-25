package com.groom.product.review.infrastructure.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.groom.common.infrastructure.feign.config.FeignConfig;
import com.groom.product.review.infrastructure.feign.dto.OrderReviewValidationRequest;
import com.groom.product.review.infrastructure.feign.dto.OrderReviewValidationResponse;

@FeignClient(
	name = "order-service",
	url = "${external.order-service.url}",
	configuration = FeignConfig.class,
	fallback = OrderClientFallback.class
)
public interface OrderClient {

	@PostMapping("/internal/api/v1/review/isReviewable")
	OrderReviewValidationResponse validateReviewOrder(
		@RequestBody OrderReviewValidationRequest request
	);
}
