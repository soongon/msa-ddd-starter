package com.example.shop.order.adapter.out.event;

import com.example.shop.order.application.port.out.OrderEventPublisher;
import com.example.shop.order.domain.OrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// 아웃바운드 어댑터: 이벤트를 로그로만 남긴다. step5에서 Kafka 어댑터로 교체한다.
@Component
class LoggingOrderEventPublisher implements OrderEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(LoggingOrderEventPublisher.class);

	@Override
	public void publish(OrderPlaced event) {
		log.info("이벤트 발행: {}", event);
	}
}
