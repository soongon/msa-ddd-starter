package com.example.shop.order.domain;

import jakarta.persistence.Embeddable;

// 값 객체: 주문 항목. 주문 애그리거트 안에서만 존재하고, 주문을 통해서만 저장되고 조회된다.
@Embeddable
public record OrderLine(Long productId, String productName, Money price, int quantity) {

	public OrderLine {
		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 1 이상이어야 한다");
		}
	}

	public Money amount() {
		return price.multiply(quantity);
	}
}
