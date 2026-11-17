package com.example.shop.order;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Money를 DB의 숫자 컬럼 하나에 저장한다. autoApply라서 Money 타입 필드에 자동 적용된다.
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Long> {

	@Override
	public Long convertToDatabaseColumn(Money money) {
		return money == null ? null : money.amount();
	}

	@Override
	public Money convertToEntityAttribute(Long amount) {
		return amount == null ? null : new Money(amount);
	}
}
