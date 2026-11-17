package com.example.shop.order.adapter.out.persistence;

import com.example.shop.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderJpaRepository extends JpaRepository<Order, String> {
}
