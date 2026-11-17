package com.example.shop.order;

import java.util.List;

import com.example.shop.member.Member;
import com.example.shop.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// step1: 상태 변경 규칙은 Order로 옮겨졌다.
// 금액 계산은 아직 여기에 남아 있고, 주문과 주문 항목도 따로 저장한다. step2에서 정리한다.
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
		Member member = findMember(request.memberId());

		if (request.items() == null || request.items().isEmpty()) {
			throw new IllegalArgumentException("주문 항목이 없습니다");
		}

		Money total = Money.ZERO;
		for (OrderItemRequest item : request.items()) {
			total = total.add(new Money(item.price()).multiply(item.quantity()));
		}

		// 할인
		Money discount = Money.ZERO;
		if (member.getGrade().equals("VIP")) {
			discount = total.percent(10); // VIP 10% 할인
		} else if (total.isGreaterThanOrEqual(new Money(100_000))) {
			discount = new Money(5_000); // 10만 원 이상 5천 원 할인
		}

		// 배송비
		Money shippingFee = new Money(3_000);
		if (total.isGreaterThanOrEqual(new Money(50_000)) || member.getGrade().equals("VIP")) {
			shippingFee = Money.ZERO; // 무료 배송
		}

		ShippingAddress address = new ShippingAddress(
				request.receiverName(), request.zipCode(), request.address1(), request.address2());
		Order order = orderDao.save(new Order(member.getId(), total, discount, shippingFee, address));

		for (OrderItemRequest item : request.items()) {
			orderItemDao.save(new OrderItem(
					order.getId(), item.productId(), item.productName(), new Money(item.price()), item.quantity()));
		}

		return order.getId();
	}

	// 장바구니 화면에서 배송비를 미리 보여줄 때 쓴다.
	@Transactional(readOnly = true)
	public long previewShippingFee(Long memberId, long totalAmount) {
		Member member = findMember(memberId);
		if (totalAmount >= 50_000 || member.getGrade().equals("VIP")) {
			return 0;
		}
		return 3_000;
	}

	public void pay(Long orderId) {
		findOrder(orderId).pay();
	}

	public void startShipping(Long orderId) {
		findOrder(orderId).startShipping();
	}

	public void cancel(Long orderId) {
		findOrder(orderId).cancel();
	}

	public void changeAddress(Long orderId, ChangeAddressRequest request) {
		findOrder(orderId).changeShippingAddress(new ShippingAddress(
				request.receiverName(), request.zipCode(), request.address1(), request.address2()));
	}

	@Transactional(readOnly = true)
	public OrderResponse getOrder(Long orderId) {
		Order order = findOrder(orderId);
		ShippingAddress address = order.getShippingAddress();
		return new OrderResponse(
				order.getId(),
				order.getMemberId(),
				order.getStatus().name(),
				order.getTotalAmount().amount(),
				order.getDiscountAmount().amount(),
				order.getShippingFee().amount(),
				order.getPaymentAmount().amount(),
				address.receiverName(),
				address.zipCode(),
				address.address1(),
				address.address2());
	}

	@Transactional(readOnly = true)
	public List<OrderItem> getOrderItems(Long orderId) {
		return orderItemDao.findByOrderId(orderId);
	}

	private Member findMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 없습니다"));
	}

	private Order findOrder(Long orderId) {
		return orderDao.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
	}
}
