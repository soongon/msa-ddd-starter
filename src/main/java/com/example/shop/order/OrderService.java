package com.example.shop.order;

import java.util.List;

import com.example.shop.member.Member;
import com.example.shop.member.MemberRepository;
import com.example.shop.order.domain.Money;
import com.example.shop.order.domain.Order;
import com.example.shop.order.domain.OrderLine;
import com.example.shop.order.domain.OrderRepository;
import com.example.shop.order.domain.ShippingAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// step2: 응용 서비스에는 업무 규칙이 거의 남지 않았다. 객체를 꺼내고, 일을 시키고, 저장한다.
// 남은 문제: 무료 배송 조건이 Order와 previewShippingFee 두 곳에 있다. step3에서 해결한다.
@Service
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;

	public OrderService(OrderRepository orderRepository, MemberRepository memberRepository) {
		this.orderRepository = orderRepository;
		this.memberRepository = memberRepository;
	}

	public Long placeOrder(PlaceOrderRequest request) {
		Member member = findMember(request.memberId());

		List<OrderLine> lines = request.items() == null ? List.of() : request.items().stream()
				.map(item -> new OrderLine(
						item.productId(), item.productName(), new Money(item.price()), item.quantity()))
				.toList();
		ShippingAddress address = new ShippingAddress(
				request.receiverName(), request.zipCode(), request.address1(), request.address2());

		Order order = Order.place(member.getId(), isVip(member), lines, address);
		return orderRepository.save(order).getId();
	}

	// 장바구니 화면에서 배송비를 미리 보여줄 때 쓴다.
	@Transactional(readOnly = true)
	public long previewShippingFee(Long memberId, long totalAmount) {
		Member member = findMember(memberId);
		if (totalAmount >= 50_000 || isVip(member)) {
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
				order.totalAmount().amount(),
				order.getDiscountAmount().amount(),
				order.getShippingFee().amount(),
				order.paymentAmount().amount(),
				address.receiverName(),
				address.zipCode(),
				address.address1(),
				address.address2());
	}

	private boolean isVip(Member member) {
		return member.getGrade().equals("VIP");
	}

	private Member findMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 없습니다"));
	}

	private Order findOrder(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
	}
}
