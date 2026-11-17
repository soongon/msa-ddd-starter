package com.example.shop;

import com.example.shop.member.Member;
import com.example.shop.member.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopApplication.class, args);
	}

	// 실습용 회원 데이터. 1번은 일반 회원, 2번은 VIP 회원이다.
	@Bean
	CommandLineRunner sampleMembers(MemberRepository memberRepository) {
		return args -> {
			if (memberRepository.count() == 0) {
				memberRepository.save(new Member("김일반", "NORMAL"));
				memberRepository.save(new Member("박브이", "VIP"));
			}
		};
	}
}
