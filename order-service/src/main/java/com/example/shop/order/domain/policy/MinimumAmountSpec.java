package com.example.shop.order.domain.policy;

import com.example.shop.order.domain.Money;

// 명세: 주문 금액이 기준 금액 이상인가
public class MinimumAmountSpec implements Specification<Money> {

	private final Money minimum;

	public MinimumAmountSpec(Money minimum) {
		this.minimum = minimum;
	}

	@Override
	public boolean isSatisfiedBy(Money orderAmount) {
		return orderAmount.isGreaterThanOrEqual(minimum);
	}
}
