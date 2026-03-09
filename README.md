# focustella-api

## 패키지 구조

이 프로젝트는 `Layered + Ports and Adapters` 성향으로 구성되어 있습니다.

```
src/main/java/com/example/focustella
├── api
│   ├── controller
│   └── dto
│       ├── request
│       └── response
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── service
├── common
│   ├── api
│   └── exception
│       └── code
├── domain
│   ├── model
│   ├── repository
│   └── service
├── infrastructure
│   ├── config
│   ├── external
│   └── persistence
│       ├── entity
│       └── repository
└── support
    ├── constants
    └── util
```

## 패키지 설명

### `api`
- `controller`: HTTP 엔드포인트를 제공하고 요청/응답을 매핑합니다.
- `dto/request`: 클라이언트 요청 스펙 DTO를 둡니다.
- `dto/response`: 클라이언트 응답 스펙 DTO를 둡니다.

### `application`
- `port/in`: 유스케이스 입력 포트(애플리케이션이 외부에 제공하는 인터페이스).
- `port/out`: 유스케이스가 필요로 하는 외부 의존 인터페이스(DB, 외부 API 등).
- `service`: 유스케이스 구현(트랜잭션 경계, 흐름 제어, 도메인 호출).

### `domain`
- `model`: 핵심 도메인 모델(엔티티/값 객체/집계 루트).
- `repository`: 도메인 관점의 저장소 인터페이스.
- `service`: 도메인 규칙/정책을 담는 도메인 서비스.

### `infrastructure`
- `config`: 스프링 설정(Security, OpenAPI, Bean 설정 등).
- `external`: 외부 시스템 연동 구현체(예: 외부 API 클라이언트).
- `persistence/entity`: JPA 엔티티 등 영속성 모델.
- `persistence/repository`: `application.port.out` 또는 `domain.repository`의 구현체.

### `common`
- `api`: 공통 응답 포맷(`ApiResponse`, `ApiError` 등).
- `exception`: 전역 예외 처리 및 비즈니스 예외.
- `exception/code`: 에러 코드 인터페이스(`ErrorCodeSpec`)와 분류별 코드(`AuthErrorCode`, `CommonErrorCode`).

### `support`
- `constants`: 전역 상수.
- `util`: 범용 유틸리티.

## 리소스 구조

```
src/main/resources
├── application.properties
├── db/migration   # Flyway/Liquibase 마이그레이션 스크립트
├── sql            # 수동 SQL 스크립트
├── static         # 정적 리소스
└── docs           # 문서 파일
```

## 테스트 구조

```
src/test/java/com/example/focustella
├── unit         # 단위 테스트
├── integration  # 통합 테스트
├── fixture      # 테스트 데이터/팩토리
└── support      # 테스트 공통 유틸
```

## 의존 방향 가이드

- `api` -> `application` -> `domain`
- `infrastructure`는 `application.port.out`을 구현
- `domain`은 프레임워크 구현 기술(`spring`, `jpa`)에 직접 의존하지 않도록 유지
- 공통 관심사는 `common`, `support`에 모아 중복을 줄임

## Docker Postgres 연결

### 1) Postgres 컨테이너 실행

```bash
docker compose up -d postgres
```

### 2) 애플리케이션 실행

```bash
./gradlew bootRun
```

기본 DB 연결 정보:
- URL: `jdbc:postgresql://localhost:5434/focustella`
- USERNAME: `focustella`
- PASSWORD: `focustella`

환경변수로 오버라이드:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

로컬 파일로 오버라이드:
- 루트의 `application-local.properties`를 사용합니다. (`.gitignore` 제외)
- `application-local.properties.example`를 복사해 값만 변경하면 됩니다.
