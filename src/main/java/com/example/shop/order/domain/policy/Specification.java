package com.example.shop.order.domain.policy;

// 명세: "대상이 조건을 만족하는가"라는 판단을 객체로 만든 것. and, or로 조합할 수 있다.
@FunctionalInterface
public interface Specification<T> {

	boolean isSatisfiedBy(T target);

	default Specification<T> and(Specification<T> other) {
		return target -> isSatisfiedBy(target) && other.isSatisfiedBy(target);
	}

	default Specification<T> or(Specification<T> other) {
		return target -> isSatisfiedBy(target) || other.isSatisfiedBy(target);
	}

	static <T> Specification<T> always() {
		return target -> true;
	}
}
