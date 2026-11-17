package com.example.shop.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.shop.order.application.port.in.PlaceOrderCommand;
import com.example.shop.order.application.port.out.MemberGradeProvider;
import com.example.shop.order.application.service.OrderCommandService;
import com.example.shop.order.domain.Money;
import com.example.shop.order.domain.Order;
import com.example.shop.order.domain.OrderId;
import com.example.shop.order.domain.OrderRepository;
import com.example.shop.order.domain.OrderStatus;
import com.example.shop.order.domain.ShippingAddress;
import org.junit.jupiter.api.Test;

// 헥사고날 구조의 이점: 포트를 가짜 구현으로 바꿔 끼우면 스프링도 DB도 없이 유스케이스를 테스트할 수 있다.
class OrderCommandServiceTest {

	static final Long NORMAL_MEMBER = 1L;
	static final Long VIP_MEMBER = 2L;

	InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
	MemberGradeProvider memberGradeProvider = memberId -> memberId.equals(VIP_MEMBER);
	OrderCommandService service = new OrderCommandService(orderRepository, memberGradeProvider);

	@Test
	void 일반회원의_5만원_미만_주문은_배송비_3천원이_붙는다() {
		OrderId orderId = service.placeOrder(command(NORMAL_MEMBER, line(10_000, 3)));

		Order order = orderRepository.findById(orderId).orElseThrow();
		assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);
		assertThat(order.totalAmount()).isEqualTo(new Money(30_000));
		assertThat(order.getShippingFee()).isEqualTo(new Money(3_000));
		assertThat(order.paymentAmount()).isEqualTo(new Money(33_000));
	}

	@Test
	void 일반회원의_10만원_이상_주문은_5천원_할인되고_무료_배송이다() {
		OrderId orderId = service.placeOrder(command(NORMAL_MEMBER, line(60_000, 2)));

		Order order = orderRepository.findById(orderId).orElseThrow();
		assertThat(order.getDiscountAmount()).isEqualTo(new Money(5_000));
		assertThat(order.getShippingFee()).isEqualTo(Money.ZERO);
		assertThat(order.paymentAmount()).isEqualTo(new Money(115_000));
	}

	@Test
	void VIP회원은_10퍼센트_할인과_무료_배송을_받는다() {
		OrderId orderId = service.placeOrder(command(VIP_MEMBER, line(10_000, 3)));

		Order order = orderRepository.findById(orderId).orElseThrow();
		assertThat(order.getDiscountAmount()).isEqualTo(new Money(3_000));
		assertThat(order.paymentAmount()).isEqualTo(new Money(27_000));
	}

	@Test
	void 주문_항목이_없으면_주문할_수_없다() {
		assertThatThrownBy(() -> service.placeOrder(command(NORMAL_MEMBER)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 배송이_시작된_주문은_취소할_수_없다() {
		OrderId orderId = service.placeOrder(command(NORMAL_MEMBER, line(10_000, 1)));
		service.pay(orderId);
		service.startShipping(orderId);

		assertThatThrownBy(() -> service.cancel(orderId)).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void 없는_주문은_취소할_수_없다() {
		assertThatThrownBy(() -> service.cancel(OrderId.newId())).isInstanceOf(IllegalArgumentException.class);
	}

	private PlaceOrderCommand command(Long memberId, PlaceOrderCommand.Line... lines) {
		return new PlaceOrderCommand(memberId, List.of(lines),
				new ShippingAddress("홍길동", "06236", "서울시 강남구", "101호"));
	}

	private PlaceOrderCommand.Line line(long price, int quantity) {
		return new PlaceOrderCommand.Line(1L, "상품", price, quantity);
	}

	// OrderRepository 포트의 테스트용 구현
	static class InMemoryOrderRepository implements OrderRepository {

		private final Map<OrderId, Order> orders = new HashMap<>();

		@Override
		public OrderId nextId() {
			return OrderId.newId();
		}

		@Override
		public Order save(Order order) {
			orders.put(order.getId(), order);
			return order;
		}

		@Override
		public Optional<Order> findById(OrderId id) {
			return Optional.ofNullable(orders.get(id));
		}
	}
}
