package com.example.shop.order.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.example.shop.order.domain.policy.OrderPolicies;
import org.junit.jupiter.api.Test;

// 애그리거트의 규칙은 스프링도 DB도 없이 검증한다.
class OrderTest {

	@Test
	void 주문_항목이_없으면_주문을_만들_수_없다() {
		assertThatThrownBy(() -> Order.place(OrderId.newId(), 1L, List.of(), address(),
				OrderPolicies.discountPolicyFor(false), OrderPolicies.shippingFeePolicyFor(false)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 주문을_만들면_주문_접수_이벤트가_등록된다() {
		Order order = place(false, line(10_000, 3));

		assertThat(order.pendingEvents()).hasSize(1);
		OrderPlaced event = (OrderPlaced) order.pendingEvents().get(0);
		assertThat(event.orderId()).isEqualTo(order.getId().value());
		assertThat(event.totalAmount()).isEqualTo(30_000);
		assertThat(event.receiverName()).isEqualTo("홍길동");
	}

	@Test
	void 주문_금액은_항목_금액의_합이다() {
		Order order = place(false, line(10_000, 2), line(5_000, 1));

		assertThat(order.totalAmount()).isEqualTo(new Money(25_000));
	}

	@Test
	void 결제_금액은_주문_금액에서_할인을_빼고_배송비를_더한_값이다() {
		Order order = place(false, line(10_000, 3));

		assertThat(order.getDiscountAmount()).isEqualTo(Money.ZERO);
		assertThat(order.getShippingFee()).isEqualTo(new Money(3_000));
		assertThat(order.paymentAmount()).isEqualTo(new Money(33_000));
	}

	@Test
	void VIP_주문은_10퍼센트_할인과_무료_배송을_받는다() {
		Order order = place(true, line(10_000, 3));

		assertThat(order.getDiscountAmount()).isEqualTo(new Money(3_000));
		assertThat(order.getShippingFee()).isEqualTo(Money.ZERO);
	}

	@Test
	void 주문_항목_목록은_밖에서_고칠_수_없다() {
		Order order = order();

		assertThatThrownBy(() -> order.getLines().add(line(1_000, 1)))
				.isInstanceOf(UnsupportedOperationException.class);
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
		assertThatThrownBy(() -> line(1_000, 0)).isInstanceOf(IllegalArgumentException.class);
	}

	private Order order() {
		return place(false, line(10_000, 3));
	}

	private Order place(boolean vip, OrderLine... lines) {
		return Order.place(OrderId.newId(), 1L, List.of(lines), address(),
				OrderPolicies.discountPolicyFor(vip), OrderPolicies.shippingFeePolicyFor(vip));
	}

	private OrderLine line(long price, int quantity) {
		return new OrderLine(1L, "상품", new Money(price), quantity);
	}

	private ShippingAddress address() {
		return new ShippingAddress("홍길동", "06236", "서울시 강남구", "101호");
	}
}
