package com.example.delivery.application;

import java.util.List;

import com.example.delivery.domain.Delivery;
import com.example.delivery.domain.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

	private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

	private final DeliveryRepository deliveryRepository;

	public DeliveryService(DeliveryRepository deliveryRepository) {
		this.deliveryRepository = deliveryRepository;
	}

	// 정책: 주문이 접수되면 배송을 준비한다.
	// 같은 이벤트가 두 번 와도 결과가 같아야 한다(멱등). 이미 준비한 주문이면 건너뛴다.
	public void prepareDelivery(String orderId, String receiverName, String zipCode, String address) {
		if (deliveryRepository.findByOrderId(orderId).isPresent()) {
			log.info("이미 배송 준비된 주문이라 건너뜀: orderId={}", orderId);
			return;
		}
		deliveryRepository.save(Delivery.prepare(orderId, receiverName, zipCode, address));
		log.info("배송 준비 시작: orderId={}, 받는 사람={}", orderId, receiverName);
	}

	public List<Delivery> findAll() {
		return deliveryRepository.findAll();
	}

	public Delivery findByOrderId(String orderId) {
		return deliveryRepository.findByOrderId(orderId)
				.orElseThrow(() -> new IllegalArgumentException("배송 정보가 없습니다"));
	}
}
