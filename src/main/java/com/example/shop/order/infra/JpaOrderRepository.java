package com.example.shop.order.infra;

import java.util.Optional;

import com.example.shop.order.domain.Order;
import com.example.shop.order.domain.OrderRepository;
import org.springframework.stereotype.Repository;

// 도메인이 선언한 OrderRepository를 JPA로 구현한다.
@Repository
class JpaOrderRepository implements OrderRepository {

	private final OrderJpaRepository jpaRepository;

	JpaOrderRepository(OrderJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public Order save(Order order) {
		return jpaRepository.save(order);
	}

	@Override
	public Optional<Order> findById(Long id) {
		return jpaRepository.findById(id);
	}
}
