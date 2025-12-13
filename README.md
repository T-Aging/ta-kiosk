# 🖥️ TA Kiosk Server (Spring Boot)

키오스크 단말과 통신하며 주문/세션/동기화의 상위 오케스트레이션을 담당하는 Spring Boot 서버입니다.
키오스크 UI와는 **WebSocket**으로 실시간 상호작용을 처리하고, 음료 추천·대화 응답은 FastAPI 기반 **AI Agent** 모듈을 호출하여 생성합니다.
또한 주문 데이터는 **RabbitMQ 기반 비동기 동기화**로 중앙(모바일) 서버/DB와 일관성을 유지합니다.
---
## ✨ 핵심 기능
### 🔌 WebSocket 세션 관리

- 키오스크 UI ↔ 서버 간 실시간 요청/응답
- 세션 기반 대화 흐름(단계/상태) 유지
### 🤖 AI Agent 연동 (FastAPI)

- 사용자 발화 → AI Agent 호출 → 추천/의도/응답 수신
- 필요 시 메뉴 스냅샷 동기화/버전 기반 처리
### 🧾 주문 도메인 처리

- 장바구니/주문 생성/옵션 반영 등 키오스크 주문 플로우 처리
### 📨 RabbitMQ 기반 Local–Central DB 동기화

- 매장(로컬) 주문 데이터를 중앙 DB로 비동기 전송
- 장애 시 재시도/지연 허용(최종 일관성)
### 🖼️ 정적 리소스/이미지 연계

- `S3 bucket` 메뉴 이미지 제공/연동
---
## 🧱 시스템 아키텍처 개요
### 🧭 구성 요소

- 🖥️ **Kiosk UI (React)**: 사용자 입력/화면 출력
- 🧩 **Kiosk Server (Spring Boot)**: WebSocket Gateway + 주문 Flow + 외부 연동
- 🤖 **AI Agent (FastAPI)**: L1/L2 캐시 + LLM(OpenAI) 기반 추천/응답 생성
- 📨 **RabbitMQ**: 로컬 ↔ 중앙 주문 데이터 비동기 전송
- 📱 **Central(Mobile) Server (Spring)**: 주문 이력/회원/마이페이지 등 중앙 기능
---
## 🔄 주요 흐름
### 1) WebSocket 기반 대화/추천 흐름
1. 키오스크 UI가 WebSocket 연결 및 세션 시작
2. 사용자 발화(텍스트/STT 결과) 전송
3. Kiosk Server가 `modules/agent`를 통해 AI Agent로 요청 전달
4. AI Agent가 캐시/LLM 기반 응답 생성 후 반환
5. Kiosk Server가 WebSocket으로 UI에 응답 전달
---
### 2) 주문 생성 & MQ 동기화 흐름
1. 키오스크에서 주문 확정(로컬 주문 생성)
2. `modules/sync/mq`에서 MQ로 주문 이벤트를 발행(RabbitMQ)
3. 중앙 서버/Sync Consumer가 주문 이벤트 수신
4. 중앙 DB에 주문 적재 → 모바일 앱에서 주문 이력 조회
---
## 🔌 Interfaces
### Endpoint: `ws://localhost:8080/ws/kiosk`
### 요청 유형:

**✅ 주문 전 작업**
- `"type": "start"`: 세션 시작
- `"type": "phone_num_login"`: 전화번호 로그인
- `"type": "qr_login"`: QR 로그인
- `"type": "recent_orders"`: 회원 최근 주문 내역
- `"type": "recent_order_detail"`: 회원 최근 주문 내역 상세보기
- `"type": "recent_order_to_cart"`: 회원 최근 주문 메뉴 주문하기
- `"type": "converse"`: LLM 기반 추천/응답

**✅ 주문 Flow**
- `"type": "order_start"`: 주문 메뉴 선택
- `"type": "select_temperature"`: 음료 온도 선택 hot/ice
- `"type": "select_size"`: 음료 size 선택
- `"type": "detail_option_yn"`: 세부 옵션 선택 여부
- `"type": "select_detail_options"`: 세부 옵션 선택
- `"type": "order_confirm"`: 주문 완료 및 대기 번호 출력

**✅ 기타 기능**
- `"type": "get_cart"`: 장바구니 확인
- `"type": "delete_cart_item"`: 장바구니에서 주문 삭제
- `"type": "session_end"`: 세션 종료
---
## 🤖 AI Agent 호출 (REST)
### Base URL: `http://localhost:8000/ta-kiosk/ai-agent`
### Endpoint: 

- POST `/session/start`
- POST `/converse`
---
## 📨 RabbitMQ

### `EXCHANGE_KIOSK = "kiosk.exchange"`
### `QUEUE_ORDER_SYNC = "order.sync.queue"`
### `ROUTING_KEY_ORDER_SYNC = "order.sync"`

---
## 📁 디렉토리 구조
```text
tak/
├── TakApplication.java                 # Spring Boot 엔트리포인트
│
├── common/                             # 공통 엔티티(도메인 모델)
│   ├── Store.java                      # 매장
│   ├── Menu.java                       # 메뉴
│   ├── OptionGroup.java                # 옵션 그룹
│   ├── OptionValue.java                # 옵션 값(로컬/중앙 매핑 기준)
│   ├── OrderHeader.java                # 주문 헤더
│   ├── OrderDetail.java                # 주문 상세
│   ├── OrderOption.java                # 주문 옵션
│   └── ... (매핑 ID, 룰 등)
│
├── config/                             # 공통 설정
│   ├── WebSocketConfig.java            # WebSocket 설정
│   ├── SecurityConfig.java             # Spring Security 설정
│   ├── RestTemplateConfig.java         # 외부 HTTP 통신(RestTemplate) 설정
│   ├── WebClientConfig.java            # WebClient 설정(사용 시)
│   ├── JacksonConfig.java              # JSON 직렬화/역직렬화 설정
│   └── CentralServerProperties.java    # 중앙 서버 연동 설정 값 바인딩
│
└── modules/
    ├── agent/                          # AI Agent 연동 모듈(FastAPI 호출)
    │   ├── client/                     # AI Agent API 호출 클라이언트
    │   ├── dto/                        # 요청/응답 DTO
    │   ├── service/                    # Agent 호출/응답 처리 서비스
    │   └── snapshot/                   # 메뉴 스냅샷 로딩/프롬프트 준비
    │
    ├── kiosk/                          # 키오스크 모듈
    │   ├── start/                      # 세션 시작 단계
    │   ├── cart/                       # 장바구니 단계
    │   ├── order/                      # 주문 Flow 단계
    │   ├── recent/                     # 최근 주문/재주문 단계
    │   ├── login/                      # 키오스크 전화번호/QR 로그인 단계
    │   ├── end/                        # 세션 종료/정리 단계
    │   ├── websocket/                  # WebSocket 핸들러/메시지 라우팅
    │   └── custom/                     # 커스텀/예외 플로우(확장 필요)
    │
    └── sync/                           # Local–Central DB 동기화 모듈
        ├── dto/                        # 동기화 메시지 DTO
        ├── mapper/                     # 엔티티 ↔ 메시지 매핑
        └── mq/                         # MQ Producer/Consumer, 라우팅키/큐 설정
```
---
## 🚀 로컬 실행
### 📦 환경 변수
```text
AI_AGENT_HOST=kiosk-fastapi
AI_AGENT_PORT=8000

CENTRAL_SERVER_HOST=mobile-spring
CENTRAL_SERVER_PORT=8081

# 로컬 DB 테스트용
KIOSK_ENDPOINT_LOCAL= YOUR_KIOSK_DB_URL_LOCAL
KIOSK_DB_USER_LOCAL= YOUR_KIOSK_DB_MASTER_ID_LOCAL
KIOSK_DB_PASS_LOCAL= YOUR_KIOSK_DB_PASSWORD_LOCAL

# 키오스크 서버 (Spring) — RDS 연결 정보 (prod)
KIOSK_ENDPOINT_PROD= YOUR_KIOSK_DB_URL_PROD
KIOSK_DB_USER_PROD= YOUR_KIOSK_DB_MASTER_ID_PROD
KIOSK_DB_PASS_PROD= YOUR_KIOSK_DB_PASSWORD_PROD

SPRING_RABBITMQ_HOST=rabbitmq
RABBITMQ_USER= YOUR_RABBITMQ_ID
RABBITMQ_PASS= YOUR_RABBITMQ_PASSWORD
```
### ▶️ Run
```text
./gradlew bootRun
```
---
## 📝 참고 사항

- 본 서버는 WebSocket 기반 키오스크 UI와 REST 기반 AI Agent 사이의 상위 계층입니다.
- 주문 데이터는 MQ 기반으로 중앙 DB에 전달되어 최종 일관성(eventual consistency) 을 가집니다.
- HTTPS 환경에서는 WebSocket이 `wss://`로 동작하도록 구성해야 합니다.
