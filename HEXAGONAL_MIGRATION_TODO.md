# 헥사고날 아키텍처 마이그레이션 TODO

## 현재 완료 상태

### ✅ Port/Adapter 패턴 전환 완료 도메인
- [x] Phase 1: Member
- [x] Phase 2: Board, Feed, DailyMessage, FanNote
- [x] Phase 3: Event, Contest
- [x] Phase 4: Like, Scrap
- [x] Phase 5: Role, Preference, Notification, Auth

### ✅ Port 인터페이스 생성 완료 도메인
- [x] Email (EmailSenderPort, VerificationCodePort)
- [x] Image (ImageUploadPort)

---

## 🔨 남은 작업

### 1. Tool 디렉토리 정리 (우선순위: 높음)

아직 삭제되지 않은 tool 디렉토리들을 정리해야 합니다.

#### Main 코드
| 도메인 | 경로 | 작업 내용 |
|--------|------|----------|
| auth | `auth/tool/` | JwtTokenProvider, OAuthMemberExtractor, MemberOrchestrator → infrastructure/adapter로 이동 |
| feed | `feed/tool/` | 남은 tool 파일 확인 및 정리 |
| contest | `contest/tool/` | 남은 tool 파일 확인 및 정리 |
| event | `event/tool/` | 남은 tool 파일 확인 및 정리 |
| email | `email/tool/` | SmtpEmailSender, RedisVerificationCodeManager → infrastructure/adapter로 이동 |

#### Test 코드
| 도메인 | 경로 | 작업 내용 |
|--------|------|----------|
| auth | `test/.../auth/tool/` | 테스트 파일 정리 또는 이동 |
| member | `test/.../member/tool/` | 테스트 파일 정리 또는 이동 |
| feed | `test/.../feed/tool/` | 테스트 파일 정리 또는 이동 |
| contest | `test/.../contest/tool/` | 테스트 파일 정리 또는 이동 |
| event | `test/.../event/tool/` | 테스트 파일 정리 또는 이동 |
| email | `test/.../email/tool/` | 테스트 파일 정리 또는 이동 |

### 2. 패키지 구조 표준화 (우선순위: 중간)

현재 tool 디렉토리에 있는 파일들을 헥사고날 표준 구조로 이동:

```
[domain]/
├── application/
│   ├── port/
│   │   ├── in/                 # Input Ports (UseCase)
│   │   └── out/                # Output Ports ✅ 대부분 완료
│   └── service/                # UseCase 구현
├── domain/                     # 순수 도메인 모델
├── infrastructure/
│   ├── adapter/                # Output Port 구현체
│   └── persistence/            # JPA 관련
└── presentation/               # Controller, DTO
```

### 3. 도메인 이벤트 도입 (우선순위: 중간)

크로스 도메인 통신을 위한 도메인 이벤트 시스템 구축:

- [ ] `shared/domain/event/DomainEvent.kt` 생성
- [ ] `shared/application/port/EventPublisherPort.kt` 생성
- [ ] `shared/infrastructure/event/SpringEventPublisherAdapter.kt` 생성
- [ ] 주요 이벤트 정의:
  - `FeedCreatedEvent`, `FeedDeletedEvent`
  - `FeedLikedEvent`, `FeedCommentCreatedEvent`
  - `MemberFollowedEvent`
  - `NotificationCreatedEvent`

### 4. Input Port (UseCase) 도입 (우선순위: 낮음)

현재는 Service가 직접 Controller에서 호출됨. UseCase 패턴 도입 가능:

```kotlin
// 현재 구조
Controller → Service

// 목표 구조
Controller → UseCase(interface) → ApplicationService(구현)
```

### 5. 순수 도메인 모델 분리 (우선순위: 낮음)

JPA 엔티티와 도메인 모델 분리:

- [ ] `domain/model/` - 순수 도메인 모델 (JPA 없음)
- [ ] `infrastructure/persistence/entity/` - JPA 엔티티
- [ ] `infrastructure/persistence/mapper/` - Entity ↔ Domain 매퍼

### 6. 테스트 구조 정리 (우선순위: 중간)

```
test/[domain]/
├── application/service/        # UseCase 단위 테스트
├── domain/                     # 순수 도메인 로직 테스트
├── infrastructure/adapter/     # Adapter 통합 테스트
└── presentation/controller/    # API E2E 테스트
```

---

## 빠른 실행 가이드

### 즉시 실행 가능한 작업

1. **남은 tool 디렉토리 확인 및 삭제**
   ```bash
   # 남은 tool 디렉토리 확인
   find src/main -type d -name "tool"
   find src/test -type d -name "tool"
   ```

2. **컴파일 확인**
   ```bash
   ./gradlew compileKotlin compileTestKotlin --quiet
   ```

3. **전체 테스트 실행**
   ```bash
   ./gradlew test
   ```

---

## 참고: 현재 Port 구조

### 생성된 Port 인터페이스 목록

```
member/application/port/out/
├── MemberQueryPort
└── MemberRepositoryPort

board/application/port/out/
├── BoardQueryPort
└── BoardRepositoryPort

feed/application/port/out/
├── FeedQueryPort, FeedRepositoryPort
├── FeedCommentQueryPort, FeedCommentRepositoryPort
├── FeedImageQueryPort, FeedImageRepositoryPort
└── FeedTagQueryPort, FeedTagRepositoryPort

like/application/port/out/
├── LikeFeedQueryPort, LikeFeedRepositoryPort
├── LikeFeedCommentQueryPort, LikeFeedCommentRepositoryPort
├── LikeDailyMessageCommentQueryPort, LikeDailyMessageCommentRepositoryPort
└── LikeBoardQueryPort, LikeBoardRepositoryPort

scrap/application/port/out/
├── SaveFeedQueryPort, SaveFeedRepositoryPort
├── SaveDailyMessageQueryPort, SaveDailyMessageRepositoryPort
├── SaveEventQueryPort, SaveEventRepositoryPort
├── SaveFanNoteQueryPort, SaveFanNoteRepositoryPort
├── SaveContestQueryPort, SaveContestRepositoryPort
└── FolderQueryPort, FolderRepositoryPort

notification/application/port/out/
├── NotificationQueryPort, NotificationRepositoryPort
├── NotificationSettingQueryPort, NotificationSettingRepositoryPort
└── FcmTokenQueryPort, FcmTokenRepositoryPort

auth/application/port/out/
├── JwtTokenPort
└── OAuthLoginPort

email/application/port/out/
├── EmailSenderPort
└── VerificationCodePort

image/application/port/out/
└── ImageUploadPort
```

---

## 마이그레이션 완료 기준

- [ ] 모든 tool 디렉토리 삭제됨
- [ ] 모든 Service가 Port 인터페이스만 의존
- [ ] 컴파일 성공
- [ ] 모든 테스트 통과
- [ ] (선택) 도메인 이벤트 시스템 도입
- [ ] (선택) Input Port(UseCase) 패턴 도입
