package com.example.shop.order;

public record OrderItemRequest(Long productId, String productName, long price, int quantity) {
}
