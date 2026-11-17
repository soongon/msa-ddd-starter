package com.example.shop.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 데이터만 담는 객체다. 업무 규칙은 전부 OrderService에 있다.
@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long memberId;
	private int status; // 0: 결제 대기, 1: 결제 완료, 2: 배송 중, 3: 배송 완료, 9: 취소
	private long totalAmount;
	private long discountAmount;
	private long shippingFee;
	private long paymentAmount;
	private String receiverName;
	private String zipCode;
	private String address1;
	private String address2;

	public Long getId() {
		return id;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(long discountAmount) {
		this.discountAmount = discountAmount;
	}

	public long getShippingFee() {
		return shippingFee;
	}

	public void setShippingFee(long shippingFee) {
		this.shippingFee = shippingFee;
	}

	public long getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(long paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}
}
