package com.example.shop.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.example.shop.member.Member;
import com.example.shop.member.MemberRepository;
import com.example.shop.order.application.port.in.PlaceOrderCommand;
import com.example.shop.order.application.port.in.PlaceOrderUseCase;
import com.example.shop.order.domain.OrderId;
import com.example.shop.order.domain.ShippingAddress;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;

// 테스트 안에서 Kafka 브로커를 띄워, 주문하면 order-events 토픽에 JSON 메시지가 실제로 들어가는지 확인한다.
@SpringBootTest
@EmbeddedKafka(topics = "order-events", partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class OrderKafkaIntegrationTest {

	@Autowired
	PlaceOrderUseCase placeOrderUseCase;

	@Autowired
	MemberRepository memberRepository;

	@Value("${spring.kafka.bootstrap-servers}")
	String bootstrapServers;

	@Test
	void 주문하면_주문_접수_이벤트가_Kafka_토픽에_발행된다() {
		Long memberId = memberRepository.save(new Member("일반회원", "NORMAL")).getId();

		OrderId orderId = placeOrderUseCase.placeOrder(new PlaceOrderCommand(
				memberId,
				List.of(new PlaceOrderCommand.Line(1L, "키보드", 30_000, 1)),
				new ShippingAddress("홍길동", "06236", "서울시 강남구", "101호")));

		try (Consumer<String, String> consumer = newConsumer()) {
			consumer.subscribe(List.of("order-events"));
			ConsumerRecord<String, String> record = pollOne(consumer);

			assertThat(record.key()).isEqualTo(orderId.value());
			assertThat(record.value())
					.contains("\"orderId\":\"" + orderId.value() + "\"")
					.contains("\"totalAmount\":30000")
					.contains("\"receiverName\":\"홍길동\"");
		}
	}

	private Consumer<String, String> newConsumer() {
		Map<String, Object> props = Map.of(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
				ConsumerConfig.GROUP_ID_CONFIG, "order-test",
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
				ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return new DefaultKafkaConsumerFactory<String, String>(props).createConsumer();
	}

	private ConsumerRecord<String, String> pollOne(Consumer<String, String> consumer) {
		long deadline = System.currentTimeMillis() + 20_000;
		while (System.currentTimeMillis() < deadline) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
			if (!records.isEmpty()) {
				return records.iterator().next();
			}
		}
		throw new AssertionError("20초 안에 메시지를 받지 못했다");
	}
}
