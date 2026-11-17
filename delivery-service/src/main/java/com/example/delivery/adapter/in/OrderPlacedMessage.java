package com.example.delivery.adapter.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 주문 서비스가 발행한 이벤트 가운데 배송이 필요한 필드만 읽는다.
// 주문 서비스의 클래스를 가져다 쓰지 않는다. 모르는 필드는 무시하므로 주문 쪽이 필드를 추가해도 깨지지 않는다.
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderPlacedMessage(
		String orderId,
		String receiverName,
		String zipCode,
		String address1,
		String address2) {
}
