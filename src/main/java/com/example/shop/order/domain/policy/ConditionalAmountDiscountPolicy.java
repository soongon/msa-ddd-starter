package com.example.shop.order.domain.policy;

import com.example.shop.order.domain.Money;

// 조건부 정액 할인: 조건(명세)을 만족하면 정해진 금액을 할인한다.
public class ConditionalAmountDiscountPolicy implements DiscountPolicy {

	private final Specification<Money> condition;
	private final Money discount;

	public ConditionalAmountDiscountPolicy(Specification<Money> condition, Money discount) {
		this.condition = condition;
		this.discount = discount;
	}

	@Override
	public Money calculateDiscount(Money orderAmount) {
		return condition.isSatisfiedBy(orderAmount) ? discount : Money.ZERO;
	}
}
