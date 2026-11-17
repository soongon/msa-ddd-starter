package com.example.delivery;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import com.example.delivery.application.DeliveryService;
import com.example.delivery.domain.Delivery;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

// 테스트 안에서 Kafka 브로커를 띄워, order-events 토픽에 들어온 메시지를 리스너가 실제로 받아 처리하는지 확인한다.
@SpringBootTest
@EmbeddedKafka(topics = "order-events", partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class DeliveryKafkaIntegrationTest {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	DeliveryService deliveryService;

	@Test
	void 토픽에_주문_접수_이벤트가_들어오면_배송이_준비된다() {
		String payload = """
				{"orderId":"order-kafka-1","memberId":1,"totalAmount":30000,
				 "receiverName":"홍길동","zipCode":"06236","address1":"서울시 강남구","address2":"101호",
				 "occurredAt":"2026-01-01T00:00:00Z"}
				""";

		kafkaTemplate.send("order-events", "order-kafka-1", payload);

		Awaitility.await().atMost(Duration.ofSeconds(20)).ignoreExceptions().untilAsserted(() -> {
			Delivery delivery = deliveryService.findByOrderId("order-kafka-1");
			assertThat(delivery.getStatus()).isEqualTo(Delivery.Status.PREPARING);
			assertThat(delivery.getReceiverName()).isEqualTo("홍길동");
		});
	}
}
