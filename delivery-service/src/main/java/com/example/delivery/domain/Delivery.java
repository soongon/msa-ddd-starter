package com.example.delivery.domain;

// 배송 컨텍스트의 모델. 주문 컨텍스트의 Order와는 다른 모델이고, 주문 ID로만 이어진다.
public class Delivery {

	public enum Status {
		PREPARING, SHIPPED, DELIVERED
	}

	private final String orderId;
	private final String receiverName;
	private final String zipCode;
	private final String address;
	private Status status;

	private Delivery(String orderId, String receiverName, String zipCode, String address) {
		this.orderId = orderId;
		this.receiverName = receiverName;
		this.zipCode = zipCode;
		this.address = address;
		this.status = Status.PREPARING;
	}

	public static Delivery prepare(String orderId, String receiverName, String zipCode, String address) {
		if (orderId == null || orderId.isBlank()) {
			throw new IllegalArgumentException("주문 ID는 필수다");
		}
		return new Delivery(orderId, receiverName, zipCode, address);
	}

	public void ship() {
		if (status != Status.PREPARING) {
			throw new IllegalStateException("배송을 시작할 수 없는 상태입니다");
		}
		this.status = Status.SHIPPED;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getAddress() {
		return address;
	}

	public Status getStatus() {
		return status;
	}
}
