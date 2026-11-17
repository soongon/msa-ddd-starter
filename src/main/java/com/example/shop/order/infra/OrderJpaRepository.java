package com.example.shop.order.infra;

import com.example.shop.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
