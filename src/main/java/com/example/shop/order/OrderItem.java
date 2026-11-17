package com.example.shop.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long orderId;
	private Long productId;
	private String productName;
	private Money price;
	private int quantity;

	protected OrderItem() {
	}

	public OrderItem(Long orderId, Long productId, String productName, Money price, int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 1 이상이어야 한다");
		}
		this.orderId = orderId;
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
	}

	public Money amount() {
		return price.multiply(quantity);
	}

	public Long getId() {
		return id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Long getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public Money getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}
}
