package com.example.shop.order;

import java.util.List;

import com.example.shop.member.Member;
import com.example.shop.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// step0: 일부러 문제를 남겨 둔 코드다. 실습 1-1에서 변경하기 어려운 지점을 찾아본다.
@Service
@Transactional
public class OrderService {

	private final OrderDao orderDao;
	private final OrderItemDao orderItemDao;
	private final MemberRepository memberRepository;

	public OrderService(OrderDao orderDao, OrderItemDao orderItemDao, MemberRepository memberRepository) {
		this.orderDao = orderDao;
		this.orderItemDao = orderItemDao;
		this.memberRepository = memberRepository;
	}

	public Long placeOrder(PlaceOrderRequest request) {
		Member member = memberRepository.findById(request.memberId())
				.orElseThrow(() -> new IllegalArgumentException("회원이 없습니다"));

		if (request.items() == null || request.items().size() == 0) {
			throw new IllegalArgumentException("주문 항목이 없습니다");
		}

		long total = 0;
		for (OrderItemRequest item : request.items()) {
			total = total + item.price() * item.quantity();
		}

		// 할인
		long discount = 0;
		if (member.getGrade().equals("VIP")) {
			discount = total / 10; // VIP 10% 할인
		} else if (total >= 100000) {
			discount = 5000; // 10만 원 이상 5천 원 할인
		}

		// 배송비
		long shippingFee = 3000;
		if (total >= 50000 || member.getGrade().equals("VIP")) {
			shippingFee = 0; // 무료 배송
		}

		Order order = new Order();
		order.setMemberId(member.getId());
		order.setStatus(0);
		order.setTotalAmount(total);
		order.setDiscountAmount(discount);
		order.setShippingFee(shippingFee);
		order.setPaymentAmount(total - discount + shippingFee);
		order.setReceiverName(request.receiverName());
		order.setZipCode(request.zipCode());
		order.setAddress1(request.address1());
		order.setAddress2(request.address2());
		orderDao.save(order);

		for (OrderItemRequest item : request.items()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(order.getId());
			orderItem.setProductId(item.productId());
			orderItem.setProductName(item.productName());
			orderItem.setPrice(item.price());
			orderItem.setQuantity(item.quantity());
			orderItemDao.save(orderItem);
		}

		return order.getId();
	}

	// 장바구니 화면에서 배송비를 미리 보여줄 때 쓴다.
	@Transactional(readOnly = true)
	public long previewShippingFee(Long memberId, long totalAmount) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 없습니다"));
		if (totalAmount >= 50000 || member.getGrade().equals("VIP")) {
			return 0;
		}
		return 3000;
	}

	public void pay(Long orderId) {
		Order order = findOrder(orderId);
		if (order.getStatus() != 0) {
			throw new IllegalStateException("결제할 수 없는 상태입니다");
		}
		order.setStatus(1);
	}

	public void startShipping(Long orderId) {
		Order order = findOrder(orderId);
		if (order.getStatus() != 1) {
			throw new IllegalStateException("배송을 시작할 수 없는 상태입니다");
		}
		order.setStatus(2);
	}

	public void cancel(Long orderId) {
		Order order = findOrder(orderId);
		if (order.getStatus() == 2 || order.getStatus() == 3) {
			throw new IllegalStateException("배송이 시작된 주문은 취소할 수 없습니다");
		}
		order.setStatus(9);
	}

	public void changeAddress(Long orderId, ChangeAddressRequest request) {
		Order order = findOrder(orderId);
		if (order.getStatus() == 2 || order.getStatus() == 3 || order.getStatus() == 9) {
			throw new IllegalStateException("배송지를 변경할 수 없는 상태입니다");
		}
		order.setReceiverName(request.receiverName());
		order.setZipCode(request.zipCode());
		order.setAddress1(request.address1());
		order.setAddress2(request.address2());
	}

	@Transactional(readOnly = true)
	public OrderResponse getOrder(Long orderId) {
		Order order = findOrder(orderId);

		String status;
		if (order.getStatus() == 0) {
			status = "PAYMENT_WAITING";
		} else if (order.getStatus() == 1) {
			status = "PAID";
		} else if (order.getStatus() == 2) {
			status = "SHIPPED";
		} else if (order.getStatus() == 3) {
			status = "DELIVERED";
		} else {
			status = "CANCELED";
		}

		return new OrderResponse(
				order.getId(),
				order.getMemberId(),
				status,
				order.getTotalAmount(),
				order.getDiscountAmount(),
				order.getShippingFee(),
				order.getPaymentAmount(),
				order.getReceiverName(),
				order.getZipCode(),
				order.getAddress1(),
				order.getAddress2());
	}

	@Transactional(readOnly = true)
	public List<OrderItem> getOrderItems(Long orderId) {
		return orderItemDao.findByOrderId(orderId);
	}

	private Order findOrder(Long orderId) {
		return orderDao.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
	}
}
