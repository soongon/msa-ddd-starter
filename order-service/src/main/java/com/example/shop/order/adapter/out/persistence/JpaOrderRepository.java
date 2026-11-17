package com.example.shop.order.adapter.out.persistence;

import java.util.Optional;

import com.example.shop.order.domain.Order;
import com.example.shop.order.domain.OrderId;
import com.example.shop.order.domain.OrderRepository;
import org.springframework.stereotype.Repository;

// 아웃바운드 어댑터: 도메인이 선언한 OrderRepository 포트를 JPA로 구현한다.
@Repository
class JpaOrderRepository implements OrderRepository {

	private final OrderJpaRepository jpaRepository;

	JpaOrderRepository(OrderJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public OrderId nextId() {
		return OrderId.newId();
	}

	@Override
	public Order save(Order order) {
		return jpaRepository.save(order);
	}

	@Override
	public Optional<Order> findById(OrderId id) {
		return jpaRepository.findById(id.value());
	}
}
