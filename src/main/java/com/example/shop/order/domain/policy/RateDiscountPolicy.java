package com.example.shop.order.domain.policy;

import com.example.shop.order.domain.Money;

// 정률 할인: 주문 금액의 일정 비율을 할인한다.
public class RateDiscountPolicy implements DiscountPolicy {

	private final int percent;

	public RateDiscountPolicy(int percent) {
		this.percent = percent;
	}

	@Override
	public Money calculateDiscount(Money orderAmount) {
		return orderAmount.percent(percent);
	}
}
