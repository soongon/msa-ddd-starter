package com.example.shop.order.application.port.in;

import com.example.shop.order.domain.OrderId;

// 인바운드 포트: 주문 서비스가 밖에 제공하는 기능
public interface PlaceOrderUseCase {

	OrderId placeOrder(PlaceOrderCommand command);
}
