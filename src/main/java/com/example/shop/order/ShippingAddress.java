package com.example.shop.order;

import jakarta.persistence.Embeddable;

// 값 객체: 배송지. 바꿀 때는 필드를 고치지 않고 통째로 교체한다.
@Embeddable
public record ShippingAddress(String receiverName, String zipCode, String address1, String address2) {

	public ShippingAddress {
		if (receiverName == null || receiverName.isBlank()) {
			throw new IllegalArgumentException("받는 사람 이름은 필수다");
		}
		if (zipCode == null || zipCode.isBlank()) {
			throw new IllegalArgumentException("우편번호는 필수다");
		}
		if (address1 == null || address1.isBlank()) {
			throw new IllegalArgumentException("주소는 필수다");
		}
	}
}
