package com.example.shop.order.domain.policy;

import com.example.shop.order.domain.Money;

// 무료 배송 조건(명세)을 만족하면 0원, 아니면 기본 배송비를 받는다.
public class ConditionalFreeShippingPolicy implements ShippingFeePolicy {

	private final Specification<Money> freeShippingCondition;
	private final Money baseFee;

	public ConditionalFreeShippingPolicy(Specification<Money> freeShippingCondition, Money baseFee) {
		this.freeShippingCondition = freeShippingCondition;
		this.baseFee = baseFee;
	}

	@Override
	public Money calculateFee(Money orderAmount) {
		return freeShippingCondition.isSatisfiedBy(orderAmount) ? Money.ZERO : baseFee;
	}
}
