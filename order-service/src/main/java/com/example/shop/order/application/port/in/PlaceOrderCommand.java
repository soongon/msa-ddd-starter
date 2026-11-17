package com.example.shop.order.application.port.in;

import java.util.List;

import com.example.shop.order.domain.ShippingAddress;

// 커맨드: "주문하기" 요청. 이벤트 스토밍의 파란 스티커에 해당한다.
public record PlaceOrderCommand(Long memberId, List<Line> lines, ShippingAddress shippingAddress) {

	public record Line(Long productId, String productName, long price, int quantity) {
	}
}
