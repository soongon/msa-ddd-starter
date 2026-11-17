package com.example.shop.order;

public record ChangeAddressRequest(String receiverName, String zipCode, String address1, String address2) {
}
