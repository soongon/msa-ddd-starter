package com.example.shop.order.adapter.in.web;

import java.net.URI;
import java.util.List;

import com.example.shop.order.application.port.in.ChangeOrderUseCase;
import com.example.shop.order.application.port.in.GetOrderQuery;
import com.example.shop.order.application.port.in.OrderView;
import com.example.shop.order.application.port.in.PlaceOrderCommand;
import com.example.shop.order.application.port.in.PlaceOrderUseCase;
import com.example.shop.order.domain.OrderId;
import com.example.shop.order.domain.ShippingAddress;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 인바운드 어댑터: HTTP 요청을 유스케이스 호출로 바꾼다. 업무 규칙은 없다.
@RestController
@RequestMapping("/orders")
public class OrderController {

	private final PlaceOrderUseCase placeOrderUseCase;
	private final ChangeOrderUseCase changeOrderUseCase;
	private final GetOrderQuery getOrderQuery;

	public OrderController(PlaceOrderUseCase placeOrderUseCase, ChangeOrderUseCase changeOrderUseCase,
			GetOrderQuery getOrderQuery) {
		this.placeOrderUseCase = placeOrderUseCase;
		this.changeOrderUseCase = changeOrderUseCase;
		this.getOrderQuery = getOrderQuery;
	}

	@PostMapping
	public ResponseEntity<Void> placeOrder(@RequestBody PlaceOrderRequest request) {
		List<PlaceOrderCommand.Line> lines = request.items() == null ? List.of() : request.items().stream()
				.map(item -> new PlaceOrderCommand.Line(
						item.productId(), item.productName(), item.price(), item.quantity()))
				.toList();
		ShippingAddress address = new ShippingAddress(
				request.receiverName(), request.zipCode(), request.address1(), request.address2());

		OrderId orderId = placeOrderUseCase.placeOrder(new PlaceOrderCommand(request.memberId(), lines, address));
		return ResponseEntity.created(URI.create("/orders/" + orderId.value())).build();
	}

	@GetMapping("/{orderId}")
	public OrderView getOrder(@PathVariable String orderId) {
		return getOrderQuery.getOrder(new OrderId(orderId));
	}

	@PostMapping("/{orderId}/payment")
	public void pay(@PathVariable String orderId) {
		changeOrderUseCase.pay(new OrderId(orderId));
	}

	@PostMapping("/{orderId}/cancellation")
	public void cancel(@PathVariable String orderId) {
		changeOrderUseCase.cancel(new OrderId(orderId));
	}

	@PutMapping("/{orderId}/shipping-address")
	public void changeAddress(@PathVariable String orderId, @RequestBody ChangeAddressRequest request) {
		changeOrderUseCase.changeShippingAddress(new OrderId(orderId), new ShippingAddress(
				request.receiverName(), request.zipCode(), request.address1(), request.address2()));
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
