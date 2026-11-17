package com.example.delivery.adapter.in;

import java.util.List;

import com.example.delivery.application.DeliveryService;
import com.example.delivery.domain.Delivery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveries")
class DeliveryController {

	record DeliveryResponse(String orderId, String receiverName, String zipCode, String address, String status) {

		static DeliveryResponse from(Delivery delivery) {
			return new DeliveryResponse(delivery.getOrderId(), delivery.getReceiverName(), delivery.getZipCode(),
					delivery.getAddress(), delivery.getStatus().name());
		}
	}

	private final DeliveryService deliveryService;

	DeliveryController(DeliveryService deliveryService) {
		this.deliveryService = deliveryService;
	}

	@GetMapping
	List<DeliveryResponse> findAll() {
		return deliveryService.findAll().stream().map(DeliveryResponse::from).toList();
	}

	@GetMapping("/{orderId}")
	DeliveryResponse findByOrderId(@PathVariable String orderId) {
		return DeliveryResponse.from(deliveryService.findByOrderId(orderId));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<String> notFound(IllegalArgumentException e) {
		return ResponseEntity.status(404).body(e.getMessage());
	}
}
