package com.example.shop.order.application.port.out;

import com.example.shop.order.domain.OrderPlaced;

// 아웃바운드 포트: 주문 이벤트를 서비스 밖으로 내보낸다.
// step4의 구현은 로그만 남긴다. step5에서 Kafka 구현으로 바뀐다.
public interface OrderEventPublisher {

	void publish(OrderPlaced event);
}
