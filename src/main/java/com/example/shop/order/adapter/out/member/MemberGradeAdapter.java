package com.example.shop.order.adapter.out.member;

import com.example.shop.member.MemberRepository;
import com.example.shop.order.application.port.out.MemberGradeProvider;
import org.springframework.stereotype.Component;

// 아웃바운드 어댑터: 회원 모듈의 표현("VIP" 문자열)을 주문이 쓰는 표현(boolean)으로 번역한다.
// 회원이 별도 서비스로 분리되면 이 클래스만 HTTP 호출로 바꾸면 된다.
@Component
class MemberGradeAdapter implements MemberGradeProvider {

	private final MemberRepository memberRepository;

	MemberGradeAdapter(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public boolean isVip(Long memberId) {
		return memberRepository.findById(memberId)
				.map(member -> "VIP".equals(member.getGrade()))
				.orElseThrow(() -> new IllegalArgumentException("회원이 없습니다"));
	}
}
