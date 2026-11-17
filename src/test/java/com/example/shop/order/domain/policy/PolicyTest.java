package com.example.shop.order.domain.policy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.example.shop.order.domain.Money;
import org.junit.jupiter.api.Test;

// 정책과 명세는 주문 없이도 따로 테스트할 수 있다.
class PolicyTest {

	@Test
	void 명세는_and와_or로_조합할_수_있다() {
		Specification<Money> atLeast50k = new MinimumAmountSpec(new Money(50_000));
		Specification<Money> atLeast100k = new MinimumAmountSpec(new Money(100_000));

		assertThat(atLeast50k.and(atLeast100k).isSatisfiedBy(new Money(70_000))).isFalse();
		assertThat(atLeast50k.or(atLeast100k).isSatisfiedBy(new Money(70_000))).isTrue();
	}

	@Test
	void 정률_할인은_주문_금액의_일정_비율이다() {
		DiscountPolicy policy = new RateDiscountPolicy(10);

		assertThat(policy.calculateDiscount(new Money(30_000))).isEqualTo(new Money(3_000));
	}

	@Test
	void 조건부_정액_할인은_조건을_만족할_때만_적용된다() {
		DiscountPolicy policy = new ConditionalAmountDiscountPolicy(
				new MinimumAmountSpec(new Money(100_000)), new Money(5_000));

		assertThat(policy.calculateDiscount(new Money(99_999))).isEqualTo(Money.ZERO);
		assertThat(policy.calculateDiscount(new Money(100_000))).isEqualTo(new Money(5_000));
	}

	@Test
	void 여러_할인_정책을_묶으면_할인액이_합쳐진다() {
		DiscountPolicy policy = new CompositeDiscountPolicy(List.of(
				new RateDiscountPolicy(10),
				new ConditionalAmountDiscountPolicy(Specification.always(), new Money(1_000))));

		assertThat(policy.calculateDiscount(new Money(30_000))).isEqualTo(new Money(4_000));
	}

	@Test
	void 무료_배송_조건을_만족하면_배송비가_0원이다() {
		ShippingFeePolicy policy = new ConditionalFreeShippingPolicy(
				new MinimumAmountSpec(new Money(50_000)), new Money(3_000));

		assertThat(policy.calculateFee(new Money(49_999))).isEqualTo(new Money(3_000));
		assertThat(policy.calculateFee(new Money(50_000))).isEqualTo(Money.ZERO);
	}

	@Test
	void 회원_등급에_따라_정책이_달라진다() {
		Money amount = new Money(30_000);

		assertThat(OrderPolicies.discountPolicyFor(true).calculateDiscount(amount)).isEqualTo(new Money(3_000));
		assertThat(OrderPolicies.discountPolicyFor(false).calculateDiscount(amount)).isEqualTo(Money.ZERO);
		assertThat(OrderPolicies.shippingFeePolicyFor(true).calculateFee(amount)).isEqualTo(Money.ZERO);
		assertThat(OrderPolicies.shippingFeePolicyFor(false).calculateFee(amount)).isEqualTo(new Money(3_000));
	}
}
