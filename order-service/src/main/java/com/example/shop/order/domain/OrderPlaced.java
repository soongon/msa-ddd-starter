package com.example.shop.order.domain;

import java.time.Instant;

// 도메인 이벤트: 주문이 접수되었다. 이름은 과거형이고 내용은 바뀌지 않는다.
// 받는 쪽(배송)이 다시 물어보지 않아도 될 만큼의 정보를 담는다.
public record OrderPlaced(
		String orderId,
		Long memberId,
		long totalAmount,
		String receiverName,
		String zipCode,
		String address1,
		String address2,
		Instant occurredAt) {
}
