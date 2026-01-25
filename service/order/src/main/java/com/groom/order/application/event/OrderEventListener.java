package com.groom.order.application.event;

import com.groom.order.domain.entity.Order;
import com.groom.common.event.OrderConfirmedEvent;
import com.groom.order.domain.repository.OrderRepository;
import com.groom.common.event.PaymentCompletedEvent;
import com.groom.common.event.PaymentFailEvent;
import com.groom.common.event.RefundFailEvent;
import com.groom.common.event.RefundSucceededEvent;
import com.groom.common.event.StockDeductedEvent;
import com.groom.common.event.StockDeductionFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Async("eventExecutor")
    @EventListener
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Payment completed for order: {}", event.orderId());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + event.orderId()));

        order.confirmPayment();
        orderRepository.save(order);
    }

    @Async("eventExecutor")
    @EventListener
    @Transactional
    public void handleStockDeducted(StockDeductedEvent event) {
        log.info("Stock deducted for order: {}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + event.getOrderId()));

        order.complete();
        orderRepository.save(order);

        eventPublisher.publishEvent(new OrderConfirmedEvent(order.getBuyerId(), order.getOrderId()));
    }

    @Async("eventExecutor")
    @EventListener
    @Transactional
    public void handlePaymentFailed(PaymentFailEvent event) {
        log.info("Payment failed for order: {}", event.orderId());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + event.orderId()));

        order.fail();
        orderRepository.save(order);
    }

    @Async("eventExecutor")
    @EventListener
    @Transactional
    public void handleStockDeductionFailed(StockDeductionFailedEvent event) {
        log.info("Stock deduction failed for order: {}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + event.getOrderId()));

        order.fail();
        orderRepository.save(order);
    }

    @Async("eventExecutor")
    @EventListener
    @Transactional
    public void handleRefundSucceeded(RefundSucceededEvent event) {
        log.info("Refund succeeded for order: {}", event.orderId());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + event.orderId()));

        order.cancel();
        orderRepository.save(order);
    }

    @Async("eventExecutor")
    @EventListener
    @Transactional
    public void handleRefundFailed(RefundFailEvent event) {
        log.error("Refund failed for order: {}", event.orderId());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + event.orderId()));

        order.requireManualCheck();
        orderRepository.save(order);
    }
}
