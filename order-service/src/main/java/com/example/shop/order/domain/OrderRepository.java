package com.example.shop.order.domain;

import java.util.Optional;

// 아웃바운드 포트: 도메인이 저장소에 요구하는 기능이다. 구현은 adapter.out.persistence에 있다.
public interface OrderRepository {

	OrderId nextId();

	Order save(Order order);

	Optional<Order> findById(OrderId id);
}
