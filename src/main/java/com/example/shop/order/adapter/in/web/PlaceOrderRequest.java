package com.example.shop.order.adapter.in.web;

import java.util.List;

public record PlaceOrderRequest(
		Long memberId,
		List<OrderItemRequest> items,
		String receiverName,
		String zipCode,
		String address1,
		String address2) {
}
