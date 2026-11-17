package com.example.shop.order.application.port.out;

// 아웃바운드 포트: 주문이 회원 쪽에 묻는 것은 "VIP인가" 하나뿐이다.
// 회원 모델 전체를 주문 안으로 끌어들이지 않는다.
public interface MemberGradeProvider {

	boolean isVip(Long memberId);
}
