package com.example.shop.order.adapter.in.web;

public record OrderItemRequest(Long productId, String productName, long price, int quantity) {
}
