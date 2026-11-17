package com.example.delivery;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.delivery.application.DeliveryService;
import com.example.delivery.domain.Delivery;
import com.example.delivery.domain.DeliveryRepository;
import org.junit.jupiter.api.Test;

class DeliveryServiceTest {

	FakeRepository repository = new FakeRepository();
	DeliveryService service = new DeliveryService(repository);

	@Test
	void 주문이_접수되면_배송_준비_상태의_배송이_만들어진다() {
		service.prepareDelivery("order-1", "홍길동", "06236", "서울시 강남구 101호");

		Delivery delivery = service.findByOrderId("order-1");
		assertThat(delivery.getStatus()).isEqualTo(Delivery.Status.PREPARING);
		assertThat(delivery.getReceiverName()).isEqualTo("홍길동");
	}

	@Test
	void 같은_주문의_이벤트가_두_번_와도_배송은_하나만_만들어진다() {
		service.prepareDelivery("order-1", "홍길동", "06236", "서울시 강남구 101호");
		service.prepareDelivery("order-1", "홍길동", "06236", "서울시 강남구 101호");

		assertThat(service.findAll()).hasSize(1);
	}

	static class FakeRepository implements DeliveryRepository {

		private final List<Delivery> deliveries = new ArrayList<>();

		@Override
		public void save(Delivery delivery) {
			deliveries.add(delivery);
		}

		@Override
		public Optional<Delivery> findByOrderId(String orderId) {
			return deliveries.stream().filter(d -> d.getOrderId().equals(orderId)).findFirst();
		}

		@Override
		public List<Delivery> findAll() {
			return List.copyOf(deliveries);
		}
	}
}
