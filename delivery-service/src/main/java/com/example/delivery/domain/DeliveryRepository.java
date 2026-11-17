package com.example.delivery.domain;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {

	void save(Delivery delivery);

	Optional<Delivery> findByOrderId(String orderId);

	List<Delivery> findAll();
}
