package com.example.shop.order.domain;

import java.util.UUID;

// 값 객체: 주문 식별자. 저장하기 전에 미리 만들 수 있어서, 주문을 만드는 순간 이벤트에 ID를 담을 수 있다.
public record OrderId(String value) {

	public OrderId {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("주문 ID는 필수다");
		}
	}

	public static OrderId newId() {
		return new OrderId(UUID.randomUUID().toString());
	}
}
