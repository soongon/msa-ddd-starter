package com.example.shop.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.example.shop.member.Member;
import com.example.shop.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

// 주문 기능의 현재 동작을 고정해 두는 테스트다.
// step1~step3에서 내부 구조를 바꿔도 이 테스트는 그대로 통과해야 한다.
@SpringBootTest
@Transactional
class OrderServiceTest {

	@Autowired
	OrderService orderService;

	@Autowired
	MemberRepository memberRepository;

	Long normalMemberId;
	Long vipMemberId;

	@BeforeEach
	void setUp() {
		normalMemberId = memberRepository.save(new Member("일반회원", "NORMAL")).getId();
		vipMemberId = memberRepository.save(new Member("VIP회원", "VIP")).getId();
	}

	@Test
	void 일반회원의_5만원_미만_주문은_배송비_3천원이_붙는다() {
		Long orderId = orderService.placeOrder(request(normalMemberId, item(10_000, 3)));

		OrderResponse order = orderService.getOrder(orderId);
		assertThat(order.status()).isEqualTo("PAYMENT_WAITING");
		assertThat(order.totalAmount()).isEqualTo(30_000);
		assertThat(order.discountAmount()).isEqualTo(0);
		assertThat(order.shippingFee()).isEqualTo(3_000);
		assertThat(order.paymentAmount()).isEqualTo(33_000);
	}

	@Test
	void 일반회원의_5만원_이상_주문은_무료_배송이다() {
		Long orderId = orderService.placeOrder(request(normalMemberId, item(25_000, 2)));

		OrderResponse order = orderService.getOrder(orderId);
		assertThat(order.shippingFee()).isEqualTo(0);
		assertThat(order.paymentAmount()).isEqualTo(50_000);
	}

	@Test
	void 일반회원의_10만원_이상_주문은_5천원_할인된다() {
		Long orderId = orderService.placeOrder(request(normalMemberId, item(60_000, 2)));

		OrderResponse order = orderService.getOrder(orderId);
		assertThat(order.totalAmount()).isEqualTo(120_000);
		assertThat(order.discountAmount()).isEqualTo(5_000);
		assertThat(order.paymentAmount()).isEqualTo(115_000);
	}

	@Test
	void VIP회원은_10퍼센트_할인과_무료_배송을_받는다() {
		Long orderId = orderService.placeOrder(request(vipMemberId, item(10_000, 3)));

		OrderResponse order = orderService.getOrder(orderId);
		assertThat(order.discountAmount()).isEqualTo(3_000);
		assertThat(order.shippingFee()).isEqualTo(0);
		assertThat(order.paymentAmount()).isEqualTo(27_000);
	}

	@Test
	void 주문_항목이_없으면_주문할_수_없다() {
		assertThatThrownBy(() -> orderService.placeOrder(request(normalMemberId)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 결제_전_주문은_취소할_수_있다() {
		Long orderId = orderService.placeOrder(request(normalMemberId, item(10_000, 1)));

		orderService.cancel(orderId);

		assertThat(orderService.getOrder(orderId).status()).isEqualTo("CANCELED");
	}

	@Test
	void 배송이_시작된_주문은_취소할_수_없다() {
		Long orderId = orderService.placeOrder(request(normalMemberId, item(10_000, 1)));
		orderService.pay(orderId);
		orderService.startShipping(orderId);

		assertThatThrownBy(() -> orderService.cancel(orderId))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void 배송_전에는_배송지를_변경할_수_있다() {
		Long orderId = orderService.placeOrder(request(normalMemberId, item(10_000, 1)));
		orderService.pay(orderId);

		orderService.changeAddress(orderId, new ChangeAddressRequest("이수령", "04524", "서울시 중구", "2층"));

		OrderResponse order = orderService.getOrder(orderId);
		assertThat(order.receiverName()).isEqualTo("이수령");
		assertThat(order.zipCode()).isEqualTo("04524");
	}

	@Test
	void 배송이_시작된_뒤에는_배송지를_변경할_수_없다() {
		Long orderId = orderService.placeOrder(request(normalMemberId, item(10_000, 1)));
		orderService.pay(orderId);
		orderService.startShipping(orderId);

		assertThatThrownBy(() -> orderService.changeAddress(orderId,
				new ChangeAddressRequest("이수령", "04524", "서울시 중구", "2층")))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void 배송비_미리보기는_주문과_같은_기준을_쓴다() {
		assertThat(orderService.previewShippingFee(normalMemberId, 49_999)).isEqualTo(3_000);
		assertThat(orderService.previewShippingFee(normalMemberId, 50_000)).isEqualTo(0);
		assertThat(orderService.previewShippingFee(vipMemberId, 1_000)).isEqualTo(0);
	}

	private PlaceOrderRequest request(Long memberId, OrderItemRequest... items) {
		return new PlaceOrderRequest(memberId, List.of(items), "홍길동", "06236", "서울시 강남구", "101호");
	}

	private OrderItemRequest item(long price, int quantity) {
		return new OrderItemRequest(1L, "상품", price, quantity);
	}
}
