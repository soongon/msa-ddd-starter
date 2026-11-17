package com.example.shop.order;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<Void> placeOrder(@RequestBody PlaceOrderRequest request) {
		Long orderId = orderService.placeOrder(request);
		return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
	}

	@GetMapping("/{orderId}")
	public OrderResponse getOrder(@PathVariable Long orderId) {
		return orderService.getOrder(orderId);
	}

	@PostMapping("/{orderId}/payment")
	public void pay(@PathVariable Long orderId) {
		orderService.pay(orderId);
	}

	@PostMapping("/{orderId}/cancellation")
	public void cancel(@PathVariable Long orderId) {
		orderService.cancel(orderId);
	}

	@PutMapping("/{orderId}/shipping-address")
	public void changeAddress(@PathVariable Long orderId, @RequestBody ChangeAddressRequest request) {
		orderService.changeAddress(orderId, request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> badRequest(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<String> conflict(IllegalStateException e) {
		return ResponseEntity.status(409).body(e.getMessage());
	}
}
