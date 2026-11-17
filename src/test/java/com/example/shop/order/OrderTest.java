package com.example.shop.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

// 규칙이 Order 안에 있으므로 스프링 없이 규칙을 검증할 수 있다.
class OrderTest {

	@Test
	void 결제_금액은_주문_금액에서_할인을_빼고_배송비를_더한_값이다() {
		Order order = new Order(1L, new Money(30_000), new Money(3_000), new Money(3_000), address());

		assertThat(order.getPaymentAmount()).isEqualTo(new Money(30_000));
	}

	@Test
	void 결제_전_주문은_취소할_수_있다() {
		Order order = order();

		order.cancel();

		assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
	}

	@Test
	void 배송이_시작된_주문은_취소할_수_없다() {
		Order order = order();
		order.pay();
		order.startShipping();

		assertThatThrownBy(order::cancel).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void 배송이_시작된_뒤에는_배송지를_변경할_수_없다() {
		Order order = order();
		order.pay();
		order.startShipping();

		assertThatThrownBy(() -> order.changeShippingAddress(address()))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void 수량이_0_이하인_주문_항목은_만들_수_없다() {
		assertThatThrownBy(() -> new OrderItem(1L, 1L, "상품", new Money(1_000), 0))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private Order order() {
		return new Order(1L, new Money(30_000), Money.ZERO, new Money(3_000), address());
	}

	private ShippingAddress address() {
		return new ShippingAddress("홍길동", "06236", "서울시 강남구", "101호");
	}
}
