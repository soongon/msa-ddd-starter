package com.example.shop.order;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// step1: 상태 변경 규칙이 OrderService에서 Order 안으로 들어왔다. setter는 없다.
@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long memberId;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	private Money totalAmount;
	private Money discountAmount;
	private Money shippingFee;
	private Money paymentAmount;

	@Embedded
	private ShippingAddress shippingAddress;

	protected Order() {
	}

	public Order(Long memberId, Money totalAmount, Money discountAmount, Money shippingFee,
			ShippingAddress shippingAddress) {
		this.memberId = memberId;
		this.status = OrderStatus.PAYMENT_WAITING;
		this.totalAmount = totalAmount;
		this.discountAmount = discountAmount;
		this.shippingFee = shippingFee;
		this.paymentAmount = totalAmount.subtract(discountAmount).add(shippingFee);
		this.shippingAddress = shippingAddress;
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

	public Money getTotalAmount() {
		return totalAmount;
	}

	public Money getDiscountAmount() {
		return discountAmount;
	}

	public Money getShippingFee() {
		return shippingFee;
	}

	public Money getPaymentAmount() {
		return paymentAmount;
	}

	public ShippingAddress getShippingAddress() {
		return shippingAddress;
	}
}
