package com.example.shop.order.domain.policy;

import com.example.shop.order.domain.Money;

// 할인 정책. 새 할인 방식이 생기면 구현체를 추가한다. 기존 코드는 고치지 않는다.
public interface DiscountPolicy {

	Money calculateDiscount(Money orderAmount);
}
