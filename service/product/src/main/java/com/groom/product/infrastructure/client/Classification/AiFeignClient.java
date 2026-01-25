package com.groom.product.infrastructure.client.Classification;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.groom.product.infrastructure.client.Classification.dto.AiFeignRequest;
import com.groom.product.infrastructure.client.Classification.dto.AiFeignResponse;

@FeignClient(
	name = "ai-classification",
	url = "${external.ai.classification.url}",
	fallback = AiFeignFallback.class
)
public interface AiFeignClient {

	@PostMapping("/infer")
	AiFeignResponse classify(AiFeignRequest request);
}
