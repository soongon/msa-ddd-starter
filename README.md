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

## 실행 (step5)

이 브랜치에는 서비스가 둘 있다.

| 서비스 | 폴더 | 포트 | 역할 |
|---|---|---|---|
| 주문 서비스 | `order-service` | 8081 | 주문을 받고 `order-events` 토픽에 주문 접수 이벤트를 발행한다 |
| 배송 서비스 | `delivery-service` | 8082 | 주문 접수 이벤트를 받아 배송을 준비한다 |

step0~step4의 실행 방법은 해당 브랜치의 README를 본다.

### 테스트

```bash
./gradlew test
```

### Docker Compose로 띄우기 (실습 6-1)

jar를 만든다.

```bash
./gradlew bootJar
```

Kafka와 두 서비스를 띄운다.

```bash
docker compose up --build -d
```

주문한다. 1번은 일반 회원, 2번은 VIP 회원이다.

```bash
curl -i -X POST localhost:8081/orders -H 'Content-Type: application/json' -d '{"memberId":1,"items":[{"productId":1,"productName":"키보드","price":30000,"quantity":1}],"receiverName":"홍길동","zipCode":"06236","address1":"서울시 강남구","address2":"101호"}'
```

배송 서비스가 이벤트를 받았는지 확인한다.

```bash
curl localhost:8082/deliveries
```

```bash
docker compose logs delivery-service | grep 배송
```

### 장애 격리 확인하기

배송 서비스를 내린다.

```bash
docker compose stop delivery-service
```

이 상태에서 위의 주문 명령을 다시 실행한다. 배송 서비스가 없어도 주문은 성공한다.

배송 서비스를 다시 올린다.

```bash
docker compose start delivery-service
```

잠시 뒤 로그를 보면, 내려가 있던 동안 쌓인 이벤트를 이어서 처리한 것을 볼 수 있다.

```bash
docker compose logs delivery-service | grep 배송
```

주의: 배송 서비스는 데이터를 메모리에 저장한다. `stop`과 `start`로 재시작하면 이전 배송 목록은 사라지고, Kafka에서 아직 읽지 않은 이벤트만 새로 처리된다.

### 정리

```bash
docker compose down
```

### PC에서 직접 실행하기

Kafka만 Docker로 띄우고 서비스는 Gradle로 실행할 수도 있다.

```bash
docker compose up -d kafka
```

```bash
./gradlew :order-service:bootRun
```

```bash
./gradlew :delivery-service:bootRun
```

주문 ID는 UUID다. 주문 요청의 응답 헤더 `Location`에 나온 주소로 조회한다.

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
