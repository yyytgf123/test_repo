package com.groom.product.review.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.groom.product.review.domain.entity.ProductRatingEntity;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRatingEntity, UUID> {

	Optional<ProductRatingEntity> findByProductId(UUID productId);
}

