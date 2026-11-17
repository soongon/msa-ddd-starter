package com.example.shop.order.domain.policy;

import java.util.List;

import com.example.shop.order.domain.Money;

// 컴포지트: 여러 할인 정책을 묶은 것도 하나의 할인 정책이다.
// 지금 업무 규칙에서는 쓰지 않는다. "행사 할인을 함께 적용한다"는 요구가 생기면 이것으로 묶는다.
public class CompositeDiscountPolicy implements DiscountPolicy {

	private final List<DiscountPolicy> policies;

	public CompositeDiscountPolicy(List<DiscountPolicy> policies) {
		this.policies = List.copyOf(policies);
	}

	@Override
	public Money calculateDiscount(Money orderAmount) {
		return policies.stream()
				.map(policy -> policy.calculateDiscount(orderAmount))
				.reduce(Money.ZERO, Money::add);
	}
}
