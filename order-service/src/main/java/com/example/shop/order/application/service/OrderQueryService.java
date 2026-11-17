package com.example.shop.order.application.service;

import com.example.shop.order.application.port.in.GetOrderQuery;
import com.example.shop.order.application.port.in.OrderView;
import com.example.shop.order.domain.Order;
import com.example.shop.order.domain.OrderId;
import com.example.shop.order.domain.OrderRepository;
import com.example.shop.order.domain.ShippingAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderQueryService implements GetOrderQuery {

	private final OrderRepository orderRepository;

	public OrderQueryService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public OrderView getOrder(OrderId orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
		ShippingAddress address = order.getShippingAddress();
		return new OrderView(
				order.getId().value(),
				order.getMemberId(),
				order.getStatus().name(),
				order.totalAmount().amount(),
				order.getDiscountAmount().amount(),
				order.getShippingFee().amount(),
				order.paymentAmount().amount(),
				address.receiverName(),
				address.zipCode(),
				address.address1(),
				address.address2());
	}
}
