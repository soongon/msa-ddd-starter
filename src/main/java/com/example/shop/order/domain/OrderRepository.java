package com.example.shop.order.domain;

import java.util.Optional;

// 리포지토리 인터페이스는 도메인 계층에 둔다. 구현은 infra 패키지에 있다.
// 애그리거트마다 하나만 만든다. OrderLine용 리포지토리는 없다.
public interface OrderRepository {

	Order save(Order order);

	Optional<Order> findById(Long id);
}
