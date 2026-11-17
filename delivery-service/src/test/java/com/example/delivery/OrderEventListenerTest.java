package com.example.delivery;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.delivery.adapter.in.OrderEventListener;
import com.example.delivery.application.DeliveryService;
import com.example.delivery.domain.Delivery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

// 스프링 컨텍스트를 띄워 리스너가 메시지를 배송으로 바꾸는지 확인한다.
// Kafka 브로커 없이 돌리기 위해 리스너 컨테이너는 시작하지 않고, 리스너 메서드를 직접 부른다.
@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
class OrderEventListenerTest {

	@Autowired
	KafkaListenerEndpointRegistry registry;

	@Autowired
	DeliveryService deliveryService;

	@Autowired
	OrderEventListener listener;

	@Test
	void 주문_서비스가_보낸_JSON을_받아_배송을_준비한다() {
		// 주문 서비스의 OrderPlaced가 직렬화된 모양이다. 배송이 쓰지 않는 필드도 들어 있다.
		String payload = """
				{"orderId":"order-42","memberId":1,"totalAmount":30000,
				 "receiverName":"홍길동","zipCode":"06236","address1":"서울시 강남구","address2":"101호",
				 "occurredAt":"2026-01-01T00:00:00Z"}
				""";

		listener.on(payload);

		Delivery delivery = deliveryService.findByOrderId("order-42");
		assertThat(delivery.getAddress()).isEqualTo("서울시 강남구 101호");
		assertThat(delivery.getStatus()).isEqualTo(Delivery.Status.PREPARING);
	}

	@Test
	void 주문_이벤트_토픽을_구독하는_리스너가_등록되어_있다() {
		assertThat(registry.getListenerContainers())
				.extracting(MessageListenerContainer::getContainerProperties)
				.anySatisfy(properties -> assertThat(properties.getTopics()).contains("order-events"));
	}
}
