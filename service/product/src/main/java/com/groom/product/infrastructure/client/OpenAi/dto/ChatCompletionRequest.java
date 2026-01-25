package com.groom.product.infrastructure.client.OpenAi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatCompletionRequest {
	private String model;
	private List<Message> messages;
	private double temperature;
}
