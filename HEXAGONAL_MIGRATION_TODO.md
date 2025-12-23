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

### ✅ Tool 디렉토리 정리 완료 (2024-12-23)

- [x] Email Domain: SmtpEmailSender → SmtpEmailSenderAdapter, RedisVerificationCodeManager →
  RedisVerificationCodeAdapter
- [x] Auth Domain: JwtTokenProvider → JwtTokenAdapter, OAuth clients → infrastructure/adapter/oauth/
- [x] Feed/Contest/Event: DtoConverter → application/service/
- [x] 모든 tool 디렉토리 삭제됨
- [x] 모든 테스트 파일 이동 완료

---

## ✅ 완료된 작업

### 1. Tool 디렉토리 정리 (완료)

모든 tool 디렉토리가 정리되어 헥사고날 표준 구조로 이동됨.

#### Email Domain

| 이전 경로                                        | 이후 경로                                                          |
|----------------------------------------------|----------------------------------------------------------------|
| `email/tool/SmtpEmailSender.kt`              | `email/infrastructure/adapter/SmtpEmailSenderAdapter.kt`       |
| `email/tool/RedisVerificationCodeManager.kt` | `email/infrastructure/adapter/RedisVerificationCodeAdapter.kt` |

#### Auth Domain

| 이전 경로                               | 이후 경로                                                 |
|-------------------------------------|-------------------------------------------------------|
| `auth/tool/JwtTokenProvider.kt`     | `auth/infrastructure/adapter/JwtTokenAdapter.kt`      |
| `auth/tool/client/*.kt`             | `auth/infrastructure/adapter/oauth/*.kt`              |
| `auth/tool/strategy/*.kt`           | `auth/infrastructure/adapter/oauth/*.kt`              |
| `auth/tool/MemberOrchestrator.kt`   | `auth/infrastructure/adapter/MemberOrchestrator.kt`   |
| `auth/tool/OAuthMemberExtractor.kt` | `auth/infrastructure/adapter/OAuthMemberExtractor.kt` |

#### DtoConverter

| 이전 경로                                 | 이후 경로                                                |
|---------------------------------------|------------------------------------------------------|
| `feed/tool/FeedDtoConverter.kt`       | `feed/application/service/FeedDtoConverter.kt`       |
| `contest/tool/ContestDtoConverter.kt` | `contest/application/service/ContestDtoConverter.kt` |
| `event/tool/EventDtoConverter.kt`     | `event/application/service/EventDtoConverter.kt`     |

### 2. 패키지 구조 표준화 (완료)

현재 구조:

```
[domain]/
├── application/
│   ├── port/
│   │   └── out/                # Output Ports ✅ 완료
│   └── service/                # DtoConverter 등 서비스 컴포넌트 ✅ 완료
├── domain/                     # 순수 도메인 모델
├── infrastructure/
│   ├── adapter/                # Output Port 구현체 ✅ 완료
│   └── persistence/            # JPA 관련
└── presentation/               # Controller, DTO
```

---

## 🔨 향후 확장 작업

### 1. 도메인 이벤트 도입 (우선순위: 중간)

크로스 도메인 통신을 위한 도메인 이벤트 시스템 구축:

- [ ] `shared/domain/event/DomainEvent.kt` 생성
- [ ] `shared/application/port/EventPublisherPort.kt` 생성
- [ ] `shared/infrastructure/event/SpringEventPublisherAdapter.kt` 생성
- [ ] 주요 이벤트 정의:
    - `FeedCreatedEvent`, `FeedDeletedEvent`
    - `FeedLikedEvent`, `FeedCommentCreatedEvent`
    - `MemberFollowedEvent`
    - `NotificationCreatedEvent`

### 2. Input Port (UseCase) 도입 (우선순위: 낮음)

현재는 Service가 직접 Controller에서 호출됨. UseCase 패턴 도입 가능:

```kotlin
// 현재 구조
Controller → Service

// 목표 구조
Controller → UseCase(interface) → ApplicationService(구현)
```

### 3. 순수 도메인 모델 분리 (우선순위: 낮음)

JPA 엔티티와 도메인 모델 분리:

- [ ] `domain/model/` - 순수 도메인 모델 (JPA 없음)
- [ ] `infrastructure/persistence/entity/` - JPA 엔티티
- [ ] `infrastructure/persistence/mapper/` - Entity ↔ Domain 매퍼

---

## 검증 명령어

```bash
# tool 디렉토리 확인 (결과 없어야 함)
find src/main -type d -name "tool"
find src/test -type d -name "tool"

# .tool. import 확인 (결과 없어야 함)
grep -r "\.tool\." src/main --include="*.kt"
grep -r "\.tool\." src/test --include="*.kt"

# 컴파일 확인
./gradlew compileKotlin compileTestKotlin --quiet

# 전체 테스트 실행
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

- [x] 모든 tool 디렉토리 삭제됨
- [x] 모든 Service가 Port 인터페이스만 의존
- [x] 컴파일 성공
- [x] 모든 테스트 통과
- [ ] (선택) 도메인 이벤트 시스템 도입
- [ ] (선택) Input Port(UseCase) 패턴 도입
