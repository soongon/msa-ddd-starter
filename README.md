# 마이크로서비스아키텍처 설계 양성 과정 스타터 저장소

온라인 쇼핑몰의 주문·배송 도메인을 3일 동안 단계적으로 발전시키는 실습 코드다.
브랜치마다 한 단계의 완성본이 들어 있다. 실습 중 따라오지 못했다면 다음 브랜치를 체크아웃해서 합류한다.

## 준비물

- JDK 17 이상
- Git
- Docker Desktop (step5에서만 사용)

Gradle은 설치하지 않아도 된다. 저장소에 포함된 `gradlew`가 내려받는다.

## 브랜치

| 브랜치 | 내용 | 교재 |
|---|---|---|
| `step0-monolith` | 규칙이 서비스 클래스에 몰려 있는 모놀리식 주문 시스템 | 1부 실습 1-1 |
| `step1-entity-vo` | 값 객체(`Money`, `ShippingAddress`)와 상태 열거형 도입, 규칙을 `Order`로 이동 | 2부 실습 2-1 중간 |
| `step2-aggregate` | 주문과 주문 항목을 하나의 애그리거트로 묶고 팩토리, 리포지토리 적용 | 2부 실습 2-1 완료 |
| `step3-refactoring` | 무료 배송 조건을 명세로, 할인과 배송비를 정책 객체로 분리 | 3부 실습 3-1 |
| `step4-order-service` | 헥사고날 구조의 주문 서비스, 도메인 이벤트 발행 | 5부 실습 5-1 |
| `step5-two-services` | 주문 서비스와 배송 서비스를 Kafka로 연동, Docker Compose | 6부 실습 6-1 |

```bash
git checkout step0-monolith
```

## 실행

```bash
./gradlew test
```

```bash
./gradlew bootRun
```

step0~step4는 저장소 루트에서 위 명령을 그대로 쓴다. step5는 서비스가 둘이므로 그 브랜치의 README를 따른다.

실행하면 회원 두 명이 미리 등록된다. 1번은 일반 회원, 2번은 VIP 회원이다.

```bash
curl -i -X POST localhost:8080/orders -H 'Content-Type: application/json' -d '{"memberId":1,"items":[{"productId":1,"productName":"키보드","price":30000,"quantity":1}],"receiverName":"홍길동","zipCode":"06236","address1":"서울시 강남구","address2":"101호"}'
```

```bash
curl localhost:8080/orders/1
```

## 업무 규칙

모든 브랜치가 같은 규칙을 구현한다. 구조만 달라진다.

- 주문 항목은 하나 이상이어야 한다.
- VIP 회원은 주문 금액의 10%를 할인받는다. 일반 회원은 10만 원 이상 주문하면 5천 원을 할인받는다.
- 배송비는 3천 원이다. 5만 원 이상 주문하거나 VIP 회원이면 무료다.
- 배송이 시작된 주문은 취소할 수 없다.
- 배송지는 배송 시작 전까지만 바꿀 수 있다.

## step0에서 찾아볼 것 (실습 1-1)

`OrderService`를 읽고 아래 질문에 답해 본다.

- 무료 배송 기준을 7만 원으로 바꾸려면 몇 군데를 고쳐야 하는가?
- 주문 상태 `2`는 무엇을 뜻하는가? 그것을 알려면 어디를 봐야 하는가?
- 수량에 음수를 넣으면 어떻게 되는가?
- 행사 할인을 추가하려면 어느 메서드를 고쳐야 하고, 그때 기존 할인이 깨질 위험은 없는가?
- 주문 기능만 따로 떼어 배포하려면 무엇이 걸리는가?
