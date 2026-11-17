package com.example.shop.order.domain.policy;

import com.example.shop.order.domain.Money;

// 회원 등급에 맞는 정책을 골라 준다. 할인과 배송비 기준 금액은 이 클래스 한 곳에만 있다.
public final class OrderPolicies {

	private static final Money BASE_SHIPPING_FEE = new Money(3_000);
	private static final Specification<Money> FREE_SHIPPING_AMOUNT = new MinimumAmountSpec(new Money(50_000));

	private static final DiscountPolicy VIP_DISCOUNT = new RateDiscountPolicy(10);
	private static final DiscountPolicy NORMAL_DISCOUNT =
			new ConditionalAmountDiscountPolicy(new MinimumAmountSpec(new Money(100_000)), new Money(5_000));

	private static final ShippingFeePolicy VIP_SHIPPING =
			new ConditionalFreeShippingPolicy(Specification.always(), BASE_SHIPPING_FEE);
	private static final ShippingFeePolicy NORMAL_SHIPPING =
			new ConditionalFreeShippingPolicy(FREE_SHIPPING_AMOUNT, BASE_SHIPPING_FEE);

	private OrderPolicies() {
	}

	public static DiscountPolicy discountPolicyFor(boolean vip) {
		return vip ? VIP_DISCOUNT : NORMAL_DISCOUNT;
	}

	public static ShippingFeePolicy shippingFeePolicyFor(boolean vip) {
		return vip ? VIP_SHIPPING : NORMAL_SHIPPING;
	}
}
