package com.example.delivery.adapter.in;

import com.example.delivery.application.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

// 인바운드 어댑터: Kafka 메시지를 배송 서비스의 유스케이스 호출로 번역한다.
// 이벤트 스토밍의 보라색 정책 스티커 "주문이 접수되면 배송을 준비한다"가 여기에 해당한다.
@Component
public class OrderEventListener {

	private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

	private final DeliveryService deliveryService;
	private final JsonMapper jsonMapper;

	OrderEventListener(DeliveryService deliveryService, JsonMapper jsonMapper) {
		this.deliveryService = deliveryService;
		this.jsonMapper = jsonMapper;
	}

	@KafkaListener(topics = "order-events")
	public void on(String payload) {
		log.info("주문 이벤트 수신: {}", payload);
		OrderPlacedMessage message = jsonMapper.readValue(payload, OrderPlacedMessage.class);
		String address = message.address2() == null || message.address2().isBlank()
				? message.address1()
				: message.address1() + " " + message.address2();
		deliveryService.prepareDelivery(message.orderId(), message.receiverName(), message.zipCode(), address);
	}
}
