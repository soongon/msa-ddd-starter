package com.example.shop.order.application.port.in;

// 조회 결과. 도메인 객체를 그대로 밖으로 내보내지 않는다.
public record OrderView(
		String id,
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
