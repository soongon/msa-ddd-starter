package com.example.shop.order;

import java.util.List;

public record PlaceOrderRequest(
		Long memberId,
		List<OrderItemRequest> items,
		String receiverName,
		String zipCode,
		String address1,
		String address2) {
}
