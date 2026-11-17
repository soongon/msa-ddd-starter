package com.example.shop.order.adapter.out.event;

import com.example.shop.order.application.port.out.OrderEventPublisher;
import com.example.shop.order.domain.OrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

// 아웃바운드 어댑터: OrderEventPublisher 포트를 Kafka로 구현한다.
// step4의 로그 어댑터를 이것으로 바꿨을 뿐, 도메인과 응용 서비스 코드는 한 줄도 바뀌지 않았다.
@Component
class KafkaOrderEventPublisher implements OrderEventPublisher {

	static final String TOPIC = "order-events";

	private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final JsonMapper jsonMapper;

	KafkaOrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate, JsonMapper jsonMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.jsonMapper = jsonMapper;
	}

	@Override
	public void publish(OrderPlaced event) {
		// 주문 ID를 키로 쓰면 같은 주문의 이벤트는 항상 같은 파티션으로 가서 순서가 지켜진다.
		kafkaTemplate.send(TOPIC, event.orderId(), jsonMapper.writeValueAsString(event));
		log.info("주문 접수 이벤트 발행: orderId={}", event.orderId());
	}
}
