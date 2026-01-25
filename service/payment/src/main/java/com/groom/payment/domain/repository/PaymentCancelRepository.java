package com.groom.payment.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.groom.payment.domain.entity.PaymentCancel;

public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, UUID> {
}
