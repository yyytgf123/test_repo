package com.groom.product.review.application.event;

import java.util.UUID;

public record ReviewCreatedEvent(
	UUID userId,
	UUID reviewId,
	UUID productId,
	int rating
) {}
