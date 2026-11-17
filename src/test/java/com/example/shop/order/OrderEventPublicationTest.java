package com.example.shop.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.shop.member.Member;
import com.example.shop.member.MemberRepository;
import com.example.shop.order.application.port.in.GetOrderQuery;
import com.example.shop.order.application.port.in.OrderView;
import com.example.shop.order.application.port.in.PlaceOrderCommand;
import com.example.shop.order.application.port.in.PlaceOrderUseCase;
import com.example.shop.order.application.port.out.OrderEventPublisher;
import com.example.shop.order.domain.OrderId;
import com.example.shop.order.domain.OrderPlaced;
import com.example.shop.order.domain.ShippingAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

// 스프링과 DB를 모두 띄워, 주문이 저장되고 커밋된 뒤 이벤트가 밖으로 나가는지 확인한다.
// 커밋 후 발행을 확인해야 하므로 테스트 메서드에 @Transactional을 붙이지 않는다.
@SpringBootTest
class OrderEventPublicationTest {

	@TestConfiguration
	static class RecordingPublisherConfig {

		@Bean
		@Primary
		RecordingPublisher recordingPublisher() {
			return new RecordingPublisher();
		}
	}

	// OrderEventPublisher 포트의 테스트용 구현: 발행된 이벤트를 모아 둔다.
	static class RecordingPublisher implements OrderEventPublisher {

		final List<OrderPlaced> published = new CopyOnWriteArrayList<>();

		@Override
		public void publish(OrderPlaced event) {
			published.add(event);
		}
	}

	@Autowired
	PlaceOrderUseCase placeOrderUseCase;

	@Autowired
	GetOrderQuery getOrderQuery;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	RecordingPublisher publisher;

	@BeforeEach
	void setUp() {
		publisher.published.clear();
	}

	@Test
	void 주문하면_주문이_저장되고_주문_접수_이벤트가_한_번_발행된다() {
		Long memberId = memberRepository.save(new Member("일반회원", "NORMAL")).getId();

		OrderId orderId = placeOrderUseCase.placeOrder(new PlaceOrderCommand(
				memberId,
				List.of(new PlaceOrderCommand.Line(1L, "키보드", 30_000, 1)),
				new ShippingAddress("홍길동", "06236", "서울시 강남구", "101호")));

		OrderView order = getOrderQuery.getOrder(orderId);
		assertThat(order.status()).isEqualTo("PAYMENT_WAITING");
		assertThat(order.paymentAmount()).isEqualTo(33_000);

		assertThat(publisher.published).hasSize(1);
		OrderPlaced event = publisher.published.get(0);
		assertThat(event.orderId()).isEqualTo(orderId.value());
		assertThat(event.memberId()).isEqualTo(memberId);
		assertThat(event.totalAmount()).isEqualTo(30_000);
		assertThat(event.zipCode()).isEqualTo("06236");
	}
}
