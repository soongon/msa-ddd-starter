package com.example.shop.order;

public enum OrderStatus {
	PAYMENT_WAITING,
	PAID,
	SHIPPED,
	DELIVERED,
	CANCELED;

	// 배송이 시작되기 전인가
	public boolean isBeforeShipping() {
		return this == PAYMENT_WAITING || this == PAID;
	}

	public boolean isCancelable() {
		return this != SHIPPED && this != DELIVERED;
	}
}
