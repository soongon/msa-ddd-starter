package com.example.shop.order.domain;

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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

// step3: 할인과 배송비 계산의 if 문이 사라졌다. 정책 객체(DiscountPolicy, ShippingFeePolicy)가 맡는다.
@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	private Order(Long memberId, List<OrderLine> lines, ShippingAddress shippingAddress) {
		this.memberId = memberId;
		this.status = OrderStatus.PAYMENT_WAITING;
		this.lines.addAll(lines);
		this.shippingAddress = shippingAddress;
	}

	// 팩토리: 주문은 반드시 이 메서드로 만든다. 만들어진 주문은 처음부터 규칙을 만족한다.
	// 할인과 배송비를 어떻게 계산하는지는 정책 객체가 안다. 주문은 정책에게 묻기만 한다.
	public static Order place(Long memberId, List<OrderLine> lines, ShippingAddress shippingAddress,
			DiscountPolicy discountPolicy, ShippingFeePolicy shippingFeePolicy) {
		if (lines == null || lines.isEmpty()) {
			throw new IllegalArgumentException("주문 항목은 하나 이상이어야 한다");
		}
		Order order = new Order(memberId, lines, shippingAddress);
		order.discountAmount = discountPolicy.calculateDiscount(order.totalAmount());
		order.shippingFee = shippingFeePolicy.calculateFee(order.totalAmount());
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

	public Long getId() {
		return id;
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
}
