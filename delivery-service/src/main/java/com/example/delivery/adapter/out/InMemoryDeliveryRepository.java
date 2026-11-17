package com.example.delivery.adapter.out;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.delivery.domain.Delivery;
import com.example.delivery.domain.DeliveryRepository;
import org.springframework.stereotype.Repository;

// 실습을 단순하게 하려고 메모리에 저장한다. 서비스를 재시작하면 데이터가 사라진다.
// 주문 서비스와 저장소를 공유하지 않는다는 점이 중요하다.
@Repository
class InMemoryDeliveryRepository implements DeliveryRepository {

	private final Map<String, Delivery> deliveries = new ConcurrentHashMap<>();

	@Override
	public void save(Delivery delivery) {
		deliveries.put(delivery.getOrderId(), delivery);
	}

	@Override
	public Optional<Delivery> findByOrderId(String orderId) {
		return Optional.ofNullable(deliveries.get(orderId));
	}

	@Override
	public List<Delivery> findAll() {
		return List.copyOf(deliveries.values());
	}
}
