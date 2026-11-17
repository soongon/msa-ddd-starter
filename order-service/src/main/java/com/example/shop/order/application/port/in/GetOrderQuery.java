package com.example.shop.order.application.port.in;

import com.example.shop.order.domain.OrderId;

// 인바운드 포트: 조회
public interface GetOrderQuery {

	OrderView getOrder(OrderId orderId);
}
