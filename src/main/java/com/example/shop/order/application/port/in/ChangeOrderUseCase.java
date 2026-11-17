package com.example.shop.order.application.port.in;

import com.example.shop.order.domain.OrderId;
import com.example.shop.order.domain.ShippingAddress;

// 인바운드 포트: 접수된 주문의 상태를 바꾸는 기능
public interface ChangeOrderUseCase {

	void pay(OrderId orderId);

	void startShipping(OrderId orderId);

	void cancel(OrderId orderId);

	void changeShippingAddress(OrderId orderId, ShippingAddress newAddress);
}
