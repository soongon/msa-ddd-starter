package com.example.shop.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemDao extends JpaRepository<OrderItem, Long> {

	List<OrderItem> findByOrderId(Long orderId);
}
