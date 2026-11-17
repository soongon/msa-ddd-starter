package com.example.shop.order.adapter.out.event;

import com.example.shop.order.application.port.out.OrderEventPublisher;
import com.example.shop.order.domain.OrderPlaced;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// 애그리거트가 등록한 도메인 이벤트를 받아, 트랜잭션이 커밋된 뒤에 서비스 밖으로 내보낸다.
// 주의: 커밋 직후 서버가 죽으면 이벤트가 유실된다. 운영에서는 트랜잭셔널 아웃박스를 쓴다(교재 5부).
@Component
class OrderEventRelay {

	private final OrderEventPublisher publisher;

	OrderEventRelay(OrderEventPublisher publisher) {
		this.publisher = publisher;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void on(OrderPlaced event) {
		publisher.publish(event);
	}
}
