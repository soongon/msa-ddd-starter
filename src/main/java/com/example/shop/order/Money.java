package com.example.shop.order;

// 값 객체: 금액. 불변이고, 생성할 때 스스로 유효성을 검사한다.
public record Money(long amount) {

	public static final Money ZERO = new Money(0);

	public Money {
		if (amount < 0) {
			throw new IllegalArgumentException("금액은 0 이상이어야 한다");
		}
	}

	public Money add(Money other) {
		return new Money(amount + other.amount);
	}

	public Money subtract(Money other) {
		return new Money(amount - other.amount);
	}

	public Money multiply(int quantity) {
		return new Money(amount * quantity);
	}

	public Money percent(int percent) {
		return new Money(amount * percent / 100);
	}

	public boolean isGreaterThanOrEqual(Money other) {
		return amount >= other.amount;
	}
}
