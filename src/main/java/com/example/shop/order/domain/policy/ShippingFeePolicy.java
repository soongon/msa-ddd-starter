package com.example.shop.order.domain.policy;

import com.example.shop.order.domain.Money;

public interface ShippingFeePolicy {

	Money calculateFee(Money orderAmount);
}
