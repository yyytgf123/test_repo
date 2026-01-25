package com.groom.product.infrastructure.client.Classification.dto;


public record AiFeignResponse(
	String category,
	double confidence
) {}
