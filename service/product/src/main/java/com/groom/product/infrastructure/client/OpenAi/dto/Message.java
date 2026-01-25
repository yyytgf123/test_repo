package com.groom.product.infrastructure.client.OpenAi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Message {
	private String role;
	private String content;
}
