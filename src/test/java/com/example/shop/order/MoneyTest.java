package com.example.shop.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

// 값 객체는 스프링도 DB도 없이 테스트할 수 있다.
class MoneyTest {

	@Test
	void 금액이_같으면_같은_객체로_본다() {
		assertThat(new Money(1_000)).isEqualTo(new Money(1_000));
	}

	@Test
	void 음수_금액은_만들_수_없다() {
		assertThatThrownBy(() -> new Money(-1)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 더하면_새_객체가_나오고_원래_객체는_바뀌지_않는다() {
		Money original = new Money(1_000);

		Money sum = original.add(new Money(500));

		assertThat(sum).isEqualTo(new Money(1_500));
		assertThat(original).isEqualTo(new Money(1_000));
	}

	@Test
	void 수량을_곱하고_비율을_구할_수_있다() {
		assertThat(new Money(10_000).multiply(3)).isEqualTo(new Money(30_000));
		assertThat(new Money(30_000).percent(10)).isEqualTo(new Money(3_000));
	}
}
