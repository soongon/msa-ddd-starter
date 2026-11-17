package com.example.shop.order.application.service;

import java.util.List;

import com.example.shop.order.application.port.in.ChangeOrderUseCase;
import com.example.shop.order.application.port.in.PlaceOrderCommand;
import com.example.shop.order.application.port.in.PlaceOrderUseCase;
import com.example.shop.order.application.port.out.MemberGradeProvider;
import com.example.shop.order.domain.Money;
import com.example.shop.order.domain.Order;
import com.example.shop.order.domain.OrderId;
import com.example.shop.order.domain.OrderLine;
import com.example.shop.order.domain.OrderRepository;
import com.example.shop.order.domain.ShippingAddress;
import com.example.shop.order.domain.policy.OrderPolicies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 응용 서비스: 업무 규칙은 없다. 객체를 만들거나 꺼내고, 일을 시키고, 저장한다.
// 포트(인터페이스)에만 의존하므로 DB나 웹 없이 테스트할 수 있다.
@Service
@Transactional
public class OrderCommandService implements PlaceOrderUseCase, ChangeOrderUseCase {

	private final OrderRepository orderRepository;
	private final MemberGradeProvider memberGradeProvider;

	public OrderCommandService(OrderRepository orderRepository, MemberGradeProvider memberGradeProvider) {
		this.orderRepository = orderRepository;
		this.memberGradeProvider = memberGradeProvider;
	}

	@Override
	public OrderId placeOrder(PlaceOrderCommand command) {
		boolean vip = memberGradeProvider.isVip(command.memberId());

		List<OrderLine> lines = command.lines() == null ? List.of() : command.lines().stream()
				.map(line -> new OrderLine(
						line.productId(), line.productName(), new Money(line.price()), line.quantity()))
				.toList();

		Order order = Order.place(
				orderRepository.nextId(),
				command.memberId(),
				lines,
				command.shippingAddress(),
				OrderPolicies.discountPolicyFor(vip),
				OrderPolicies.shippingFeePolicyFor(vip));
		orderRepository.save(order);
		return order.getId();
	}

	@Override
	public void pay(OrderId orderId) {
		findOrder(orderId).pay();
	}

	@Override
	public void startShipping(OrderId orderId) {
		findOrder(orderId).startShipping();
	}

	@Override
	public void cancel(OrderId orderId) {
		findOrder(orderId).cancel();
	}

	@Override
	public void changeShippingAddress(OrderId orderId, ShippingAddress newAddress) {
		findOrder(orderId).changeShippingAddress(newAddress);
	}

	private Order findOrder(OrderId orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
	}
}
