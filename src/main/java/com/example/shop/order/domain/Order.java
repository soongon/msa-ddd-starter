package com.example.shop.order.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.shop.order.domain.policy.DiscountPolicy;
import com.example.shop.order.domain.policy.ShippingFeePolicy;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import org.springframework.data.domain.AbstractAggregateRoot;

// step4: 주문이 만들어질 때 OrderPlaced 이벤트를 등록한다.
// AbstractAggregateRoot는 등록된 이벤트를 리포지토리 save() 시점에 발행해 준다.
@Entity
@Table(name = "orders")
public class Order extends AbstractAggregateRoot<Order> {

	@Id
	private String id;

	// 회원은 다른 애그리거트이므로 객체가 아니라 ID로 참조한다.
	private Long memberId;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@ElementCollection
	@CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
	@OrderColumn(name = "line_idx")
	private List<OrderLine> lines = new ArrayList<>();

	private Money discountAmount;
	private Money shippingFee;

	@Embedded
	private ShippingAddress shippingAddress;

	protected Order() {
	}

	private Order(OrderId id, Long memberId, List<OrderLine> lines, ShippingAddress shippingAddress) {
		this.id = id.value();
		this.memberId = memberId;
		this.status = OrderStatus.PAYMENT_WAITING;
		this.lines.addAll(lines);
		this.shippingAddress = shippingAddress;
	}

	// 팩토리: 주문은 반드시 이 메서드로 만든다.
	public static Order place(OrderId id, Long memberId, List<OrderLine> lines, ShippingAddress shippingAddress,
			DiscountPolicy discountPolicy, ShippingFeePolicy shippingFeePolicy) {
		if (lines == null || lines.isEmpty()) {
			throw new IllegalArgumentException("주문 항목은 하나 이상이어야 한다");
		}
		Order order = new Order(id, memberId, lines, shippingAddress);
		order.discountAmount = discountPolicy.calculateDiscount(order.totalAmount());
		order.shippingFee = shippingFeePolicy.calculateFee(order.totalAmount());
		order.registerEvent(new OrderPlaced(
				id.value(),
				memberId,
				order.totalAmount().amount(),
				shippingAddress.receiverName(),
				shippingAddress.zipCode(),
				shippingAddress.address1(),
				shippingAddress.address2(),
				Instant.now()));
		return order;
	}

	// 불변식: 주문 금액은 항상 항목 금액의 합이다. 저장하지 않고 계산하므로 어긋날 수 없다.
	public Money totalAmount() {
		return lines.stream()
				.map(OrderLine::amount)
				.reduce(Money.ZERO, Money::add);
	}

	public Money paymentAmount() {
		return totalAmount().subtract(discountAmount).add(shippingFee);
	}

	public void pay() {
		if (status != OrderStatus.PAYMENT_WAITING) {
			throw new IllegalStateException("결제할 수 없는 상태입니다");
		}
		this.status = OrderStatus.PAID;
	}

	public void startShipping() {
		if (status != OrderStatus.PAID) {
			throw new IllegalStateException("배송을 시작할 수 없는 상태입니다");
		}
		this.status = OrderStatus.SHIPPED;
	}

	public void cancel() {
		if (!status.isCancelable()) {
			throw new IllegalStateException("배송이 시작된 주문은 취소할 수 없습니다");
		}
		this.status = OrderStatus.CANCELED;
	}

	public void changeShippingAddress(ShippingAddress newAddress) {
		if (!status.isBeforeShipping()) {
			throw new IllegalStateException("배송지를 변경할 수 없는 상태입니다");
		}
		this.shippingAddress = newAddress;
	}

	public OrderId getId() {
		return new OrderId(id);
	}

	public Long getMemberId() {
		return memberId;
	}

	public OrderStatus getStatus() {
		return status;
	}

	// 밖에서 목록을 직접 고치지 못하도록 읽기 전용으로 내준다.
	public List<OrderLine> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public Money getDiscountAmount() {
		return discountAmount;
	}

	public Money getShippingFee() {
		return shippingFee;
	}

	public ShippingAddress getShippingAddress() {
		return shippingAddress;
	}

	// 테스트에서 등록된 이벤트를 확인할 때 쓴다.
	public List<Object> pendingEvents() {
		return List.copyOf(domainEvents());
	}
}
