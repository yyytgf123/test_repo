package com.groom.product.infrastructure.client.OpenAi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.groom.product.infrastructure.client.OpenAi.config.OpenAiFeignConfig;
import com.groom.product.infrastructure.client.OpenAi.dto.ChatCompletionRequest;
import com.groom.product.infrastructure.client.OpenAi.dto.ChatCompletionResponse;

@FeignClient(
	name = "openai-client",
	url = "${external.ai.openai.base-url}",
	configuration = OpenAiFeignConfig.class,
	fallback = OpenAiFeignFallback.class
)
public interface OpenAiFeignClient {

	@PostMapping("/v1/chat/completions")
	ChatCompletionResponse createChatCompletion(
		ChatCompletionRequest request
	);
}
