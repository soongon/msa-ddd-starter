package com.example.shop.order;

public record OrderResponse(
		Long id,
		Long memberId,
		String status,
		long totalAmount,
		long discountAmount,
		long shippingFee,
		long paymentAmount,
		String receiverName,
		String zipCode,
		String address1,
		String address2) {
}
