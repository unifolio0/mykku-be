# MyKKU Backend Project Guide for Claude

## Project Overview

MyKKU는 덕질 커뮤니티 플랫폼의 백엔드 서비스입니다.

## Architecture and Dependency Rules

### JPA Entity Relationship Rules

#### OneToMany 관계 사용 금지
- **절대 OneToMany 관계를 사용하지 마세요**
- 부모 엔티티에서 자식 컬렉션을 관리하는 것은 성능 및 메모리 문제를 야기할 수 있습니다
- 대신 다음 패턴을 사용하세요:
  - 자식 엔티티에서 ManyToOne 관계만 사용
  - 부모 엔티티의 자식들이 필요한 경우, 별도의 Repository 메서드로 조회
  - Tool 계층에서 부모와 자식을 각각 조회하여 Service에서 조합

#### 예시
```kotlin
// ❌ 잘못된 예시 - OneToMany 사용
@Entity
class FanNote {
    @OneToMany(mappedBy = "fanNote")
    val pages: List<FanNotePage> = mutableListOf()
}

// ✅ 올바른 예시 - ManyToOne만 사용
@Entity
class FanNotePage {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_note_id")
    var fanNote: FanNote? = null
}

// Tool 계층에서 별도 조회
class FanNoteReader {
    fun findById(id: Long): FanNote { ... }
    fun findPagesByFanNoteId(fanNoteId: Long): List<FanNotePage> { ... }
}
```

### Layer Architecture

이 프로젝트는 명확한 계층 구조를 따릅니다. 각 계층은 특정한 의존성 규칙을 준수해야 합니다:

#### 1. Controller Layer

- **역할**: HTTP 요청/응답 처리, API 엔드포인트 정의
- **의존성 규칙**: Service 계층에만 의존
- **금지사항**:
  - Repository나 Tool 계층에 직접 의존 불가
  - **Domain 패키지 import 금지 - DTO만 사용**
  - **Domain 객체를 직접 반환하거나 받으면 안 됨**
- **DTO 사용 규칙**:
  - Controller는 Request DTO를 받아 Service에 전달
  - Service는 Response DTO로 변환하여 Controller에 반환
  - Controller에서 Domain 객체를 직접 다루지 않음
- **예외**:
  - **@CurrentMember 어노테이션을 통한 Member 파라미터 수신은 허용**
  - 이는 인증된 사용자 정보를 Controller에서 Service로 전달하기 위한 필수 패턴
  - 예: `fun createFolder(@CurrentMember member: Member, ...)`
- **예시**:
  ```kotlin
  // ❌ 잘못된 예시 - Domain 객체 반환
  @RestController
  class FeedController(
      private val feedService: FeedService
  ) {
      @GetMapping
      fun getFeeds(): ResponseEntity<ApiResponse<Page<Feed>>> {  // Domain 객체 Feed 반환 - 금지!
          val feeds = feedService.getFeeds()
          return ResponseEntity.ok(ApiResponse(data = feeds))
      }
  }

  // ✅ 올바른 예시 - DTO 반환
  @RestController
  class FeedController(
      private val feedService: FeedService  // ✅ Service 계층만 의존
  ) {
      @GetMapping
      fun getFeeds(): ResponseEntity<ApiResponse<Page<FeedResponse>>> {  // DTO 반환
          val feeds = feedService.getFeeds()  // Service가 DTO로 변환해서 반환
          return ResponseEntity.ok(ApiResponse(data = feeds))
      }
  }
  ```

#### 2. Service Layer

- **역할**: 비즈니스 로직 처리, 트랜잭션 관리
- **의존성 규칙**: Tool 계층에만 의존
- **금지사항**: Repository 계층에 직접 의존 불가
- **예시**:
  ```kotlin
  @Service
  class FeedService(
      private val feedReader: FeedReader,  // ✅ Tool 계층 의존
      private val feedWriter: FeedWriter,  // ✅ Tool 계층 의존
      // private val feedRepository: FeedRepository  // ❌ Repository 직접 의존 금지
  )
  ```

#### 3. Tool Layer

- **역할**: 데이터 접근 로직 캡슐화, Repository 조작
- **의존성 규칙**: Repository 계층이나 다른 Tool 계층에 의존 가능
- **예시**:
  ```kotlin
  @Component
  class FeedReader(
      private val feedRepository: FeedRepository,  // ✅ Repository 의존 가능
      private val memberReader: MemberReader  // ✅ 다른 Tool 의존 가능
  )
  ```

#### 4. Repository Layer

- **역할**: 데이터베이스 접근, JPA 인터페이스
- **의존성 규칙**: 최하위 계층으로 다른 계층에 의존하지 않음
- **예시**:
  ```kotlin
  @Repository
  interface FeedRepository : JpaRepository<Feed, Long> {
      // 순수한 데이터 접근 메서드만 정의
  }
  ```

### 의존성 방향

```
Controller → Service → Tool → Repository
                         ↓
                    Other Tools
```

## Testing Guidelines

### 각 계층별 테스트 작성 규칙

#### 1. Controller Tests

- 통합 테스트 작성 (`@SpringBootTest`)
- REST Docs 테스트 작성
- 실제 HTTP 요청/응답 검증

#### 2. Service Tests

- 단위 테스트 작성 (`@ExtendWith(MockitoExtension::class)`)
- Tool 계층 Mock 처리
- 비즈니스 로직 검증

#### 3. Tool Tests

- 단위 테스트 작성
- Repository Mock 처리
- 데이터 변환 로직 검증

#### 4. Repository Tests

- `@DataJpaTest` 사용
- 실제 데이터베이스 쿼리 검증
- 커스텀 쿼리 메서드 테스트

## Code Style Guidelines

### Code Comments

- **절대 주석을 작성하지 마세요**
- 코드는 자체적으로 명확해야 하며, 주석이 필요하다면 코드를 리팩토링하세요
- 예외: 
  - 복잡한 알고리즘의 수학적 설명이 필요한 경우
  - 외부 API나 라이브러리의 특이한 동작을 설명해야 하는 경우
- 일반적인 비즈니스 로직이나 단순한 구현에는 주석을 사용하지 마세요

```kotlin
// ❌ 잘못된 예시
fun sendEmail(email: String) {
    // 이메일 유효성 검사
    if (!isValidEmail(email)) {
        throw InvalidEmailException()
    }
    // 이메일 발송
    emailSender.send(email)
}

// ✅ 올바른 예시 - 주석 없이 명확한 코드
fun sendEmail(email: String) {
    validateEmail(email)
    emailSender.send(email)
}
```

## Code Style Guidelines

### Kotlin Conventions

- 함수명과 변수명은 camelCase 사용
- 클래스명은 PascalCase 사용
- 상수는 UPPER_SNAKE_CASE 사용
- 최대 라인 길이: 120자

### Import Order

1. Java imports
2. Kotlin imports
3. Spring imports
4. Project imports
5. Static imports

### Documentation

- 복잡한 비즈니스 로직에는 KDoc 주석 추가
- REST API는 RestDocs로 문서화
- 테스트 메서드명은 한글로 작성 가능

## Common Patterns

### Pagination

- Spring Data의 `Pageable`과 `Page` 사용
- 기본 페이지 크기: 20
- Sort 기본값: createdAt DESC

### Exception Handling (도메인별 예외 처리 구조)

#### 개요
- 각 도메인이 독립적인 예외 처리 체계를 보유
- 도메인별 예외 클래스, 에러 코드, 핸들러 분리
- 공통 기반 구조를 상속받아 일관성 유지

#### 예외 처리 구조
```
common/exception/
├── DomainErrorCode.kt         # 모든 도메인 에러 코드가 구현할 인터페이스
├── BaseException.kt            # 모든 커스텀 예외의 추상 클래스
├── BaseDomainException.kt      # 도메인 예외의 추상 클래스
├── BaseExceptionHandler.kt     # 공통 예외 처리 핸들러
├── CommonErrorCode.kt          # 공통 에러 코드 (Validation 등)
└── CommonException.kt          # 공통 예외 클래스

[domain]/exception/
├── [Domain]ErrorCode.kt        # 도메인별 에러 코드 enum
├── [Domain]Exception.kt        # 도메인별 예외 클래스
└── [Domain]ExceptionHandler.kt # 도메인별 예외 핸들러
```

#### 예외 처리 예시
```kotlin
// Feed 도메인 예외 정의
enum class FeedErrorCode(
    override val status: HttpStatus,
    override val message: String
) : DomainErrorCode {
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "피드를 찾을 수 없습니다")
}

// Feed 도메인 예외 클래스
class FeedException(
    errorCode: FeedErrorCode
) : BaseDomainException(errorCode) {
    companion object {
        fun feedNotFound() = FeedException(FeedErrorCode.FEED_NOT_FOUND)
    }
}

// 사용 예시
throw FeedException.feedNotFound()
```

#### Handler 우선순위
- 도메인별 핸들러: `@Order(Ordered.HIGHEST_PRECEDENCE)` 
- 공통 핸들러: `@Order(Ordered.LOWEST_PRECEDENCE)`
- 도메인 핸들러 → 공통 핸들러 순서로 처리

### Transaction Management

- Service 계층에서 `@Transactional` 사용
- 읽기 전용 메서드는 `@Transactional(readOnly = true)` 사용

### Exception Testing Rules

#### 예외 검증 시 ErrorCode Enum 사용 필수

- **테스트에서 예외 검증 시 반드시 ErrorCode enum 사용**
- **예외 메시지 문자열 직접 비교 금지**
- ErrorCode를 통한 검증으로 일관성과 유지보수성 확보

```kotlin
// ❌ 잘못된 예시 - 메시지 문자열 직접 비교
val exception = assertThrows<ScrapException> {
    folderReader.getFolderById(999L, member)
}
assertEquals("폴더를 찾을 수 없습니다", exception.message)

// ✅ 올바른 예시 - ErrorCode enum 사용
val exception = assertThrows<ScrapException> {
    folderReader.getFolderById(999L, member)
}
assertEquals(ScrapErrorCode.FOLDER_NOT_FOUND, exception.errorCode)
```

#### 이유
- 예외 메시지는 언제든 변경될 수 있음
- ErrorCode는 계약(Contract)으로서 안정적
- 리팩토링 시 타입 안정성 제공
- IDE의 자동 완성 및 타입 체크 활용 가능

## Directory Structure

```
src/main/kotlin/com/example/mykku/
├── [domain]/
│   ├── domain/        # Entity 클래스
│   ├── dto/           # DTO 클래스
│   ├── exception/     # 도메인별 예외 처리 (ErrorCode, Exception, Handler)
│   ├── repository/    # Repository 인터페이스
│   ├── tool/          # Tool 계층 (Reader, Writer)
│   ├── [Domain]Controller.kt
│   └── [Domain]Service.kt
├── auth/              # 인증/인가
├── common/            
│   ├── domain/        # 공통 Entity 기반 클래스
│   ├── exception/     # 공통 예외 처리 구조
│   └── util/          # 공통 유틸리티
├── config/            # 설정 클래스
└── image/             # 이미지 업로드
```

## Development Workflow

1. **새 기능 개발 시**:
    - Tool 계층부터 시작 (Repository 접근 로직)
    - Service 계층 구현 (비즈니스 로직)
    - Controller 계층 구현 (API 엔드포인트)
    - 테스트 작성 (단위 테스트 → 통합 테스트)
    - RestDocs 문서화

2. **리팩토링 시**:
    - 계층 간 의존성 규칙 확인
    - Tool 계층으로 Repository 로직 이동
    - 테스트 업데이트

3. **코드 리뷰 체크리스트**:
    - [ ] 계층 간 의존성 규칙 준수
    - [ ] 적절한 Tool 계층 사용
    - [ ] 트랜잭션 처리 확인
    - [ ] 테스트 커버리지 확인
    - [ ] RestDocs 문서화

## Testing Requirements

새로운 기능을 추가할 때는 **반드시** 다음 5가지 계층의 테스트를 모두 작성해야 합니다:

### 1. RestDocsTest
- **위치**: `src/test/kotlin/com/example/mykku/docs/[Domain]ControllerRestDocsTest.kt`
- **상속**: `BaseControllerRestDocsTest`를 상속
- **목적**: API 문서 자동 생성
- **필수 요소**:
  - `@Mock` Service 주입
  - `@InjectMocks` Controller 주입
  - MockMvcBuilders.standaloneSetup() 사용
  - document() 메서드로 API 문서화
  - 요청/응답 필드 상세 설명

### 2. ControllerTest
- **위치**: `src/test/kotlin/com/example/mykku/[domain]/[Domain]ControllerTest.kt`
- **어노테이션**: `@WebMvcTest([Domain]Controller::class)`
- **목적**: Controller 단위 테스트
- **필수 요소**:
  - `@MockBean` Service 주입
  - HTTP 요청/응답 검증
  - 에러 케이스 테스트

### 3. ServiceTest
- **위치**: `src/test/kotlin/com/example/mykku/[domain]/[Domain]ServiceTest.kt`
- **어노테이션**: `@ExtendWith(MockitoExtension::class)`
- **목적**: 비즈니스 로직 테스트
- **필수 요소**:
  - `@Mock` Tool 계층 주입
  - `@InjectMocks` Service 주입
  - 정상 케이스와 예외 케이스 테스트

### 4. ToolTest
- **위치**: `src/test/kotlin/com/example/mykku/[domain]/tool/[Tool]Test.kt`
- **어노테이션**: `@ExtendWith(MockitoExtension::class)`
- **목적**: 데이터 접근 로직 테스트
- **필수 요소**:
  - `@Mock` Repository 주입
  - `@InjectMocks` Tool (Reader/Writer) 주입
  - 데이터 변환 로직 검증

### 5. RepositoryTest
- **위치**: `src/test/kotlin/com/example/mykku/[domain]/repository/[Domain]RepositoryTest.kt`
- **어노테이션**: `@DataJpaTest`
- **목적**: 실제 데이터베이스 쿼리 테스트
- **필수 요소**:
  - TestEntityManager 사용
  - 실제 JPA 쿼리 동작 검증
  - 커스텀 쿼리 메서드 테스트

### API 문서화 (RestDocs)

RestDocsTest 작성 후 반드시:
1. `src/docs/asciidoc/index.adoc` 파일에 API 섹션 추가
2. operation 스니펫 참조 추가
3. 다음 형식 준수:
```asciidoc
[[domain-api]]
== 도메인 API

=== API 이름

API 설명

operation::operation-name[snippets='http-request,request-fields,http-response,response-fields']
```

### 테스트 작성 체크리스트
- [ ] RestDocsTest 작성 및 API 문서 생성
- [ ] ControllerTest로 엔드포인트 검증
- [ ] ServiceTest로 비즈니스 로직 검증
- [ ] ToolTest로 데이터 접근 로직 검증
- [ ] RepositoryTest로 실제 쿼리 동작 확인
- [ ] index.adoc에 API 문서 섹션 추가

## Clean Code Rules

### 메서드 복잡도 규칙

#### 1. Indent Depth (최대 2단계)
- **규칙**: 한 메서드 내 중첩 깊이는 최대 2까지만 허용
- **금지 사항**: 
  - 3중첩 이상의 if문 금지
  - for 안에 if 안에 if와 같은 3중첩 구조 금지
- **해결 방법**:
  - Early return 패턴 활용
  - 복잡한 조건을 별도 메서드로 추출
  - Guard clause 패턴 사용
  ```kotlin
  // ❌ 잘못된 예시 - 3중첩
  fun processData(data: List<Item>) {
      for (item in data) {
          if (item.isValid()) {
              if (item.needsProcessing()) {
                  // 처리 로직
              }
          }
      }
  }
  
  // ✅ 올바른 예시 - 최대 2중첩
  fun processData(data: List<Item>) {
      for (item in data) {
          if (!item.isValid() || !item.needsProcessing()) continue
          // 처리 로직
      }
  }
  ```

#### 2. Method Length (최대 15줄)
- **규칙**: 모든 메서드는 15줄 이내로 작성
- **측정 기준**: 메서드 시그니처부터 닫는 중괄호까지
- **해결 방법**:
  - 단일 책임 원칙 적용
  - 복잡한 로직을 여러 작은 메서드로 분할
  - Tool 계층으로 세부 구현 위임

### 계층별 책임 분리

#### Controller Layer
- **책임**: HTTP 요청/응답 처리에만 집중
- **규칙**: 
  - Service 계층에만 의존
  - 비즈니스 로직 포함 금지
  - 요청 검증과 응답 변환만 수행

#### Service Layer  
- **책임**: 비즈니스 로직의 흐름 제시
- **규칙**:
  - Tool 계층에만 의존
  - 세부 구현은 Tool 계층에 위임
  - 트랜잭션 경계 설정
  - 여러 Tool을 조합하여 비즈니스 요구사항 구현

#### Tool Layer
- **책임**: 비즈니스 로직의 세부 구현
- **규칙**:
  - Repository나 다른 Tool 계층에 의존 가능
  - 데이터 접근 로직 캡슐화
  - 복잡한 쿼리나 데이터 변환 로직 처리

#### Repository Layer
- **책임**: 데이터베이스 접근
- **규칙**:
  - 최하위 계층으로 다른 계층에 의존 금지
  - 순수 JPA 인터페이스만 정의

### 의존성 규칙

#### 절대 규칙
- **Controller → Service → Tool → Repository** 순서 엄격 준수
- **역방향 의존성 금지**: 하위 계층이 상위 계층 참조 불가
- **계층 건너뛰기 금지**: Controller에서 Tool/Repository 직접 접근 불가
- **Service에서 Repository 직접 접근 금지**: 반드시 Tool 계층 경유

#### 위반 예시와 해결
```kotlin
// ❌ 잘못된 예시 - Service에서 Repository 직접 의존
@Service
class FeedService(
    private val feedRepository: FeedRepository  // 금지!
)

// ✅ 올바른 예시 - Service는 Tool에만 의존
@Service
class FeedService(
    private val feedReader: FeedReader,
    private val feedWriter: FeedWriter
)
```

### 코드 중복 제거

#### 중복 제거 원칙
- **DRY (Don't Repeat Yourself)**: 동일한 로직 반복 금지
- **공통 로직 추출**: 
  - 유사한 패턴은 제네릭 메서드로 통합
  - 반복되는 검증 로직은 공통 유틸리티로 추출
  - 비슷한 DTO 변환은 Converter 클래스로 통합

#### 중복 패턴 식별
- OAuth 인증의 createOrUpdate 로직
- 페이지네이션 처리 로직
- 예외 처리 패턴
- DTO 변환 로직

## Important Notes

- **절대 Service에서 Repository를 직접 주입하지 마세요**
- **Controller에서는 오직 Service만 사용하세요**
- **Controller는 DTO만 사용하고, Domain 객체를 직접 반환하거나 받지 마세요**
- **Service는 Controller에 Domain이 아닌 DTO를 반환해야 합니다**
- **Controller에서 domain 패키지를 import하면 안 됩니다**
  - **예외: @CurrentMember 어노테이션을 통한 Member 파라미터 수신은 허용됩니다**
- **복잡한 쿼리는 Tool 계층에 캡슐화하세요**
- **페이지네이션은 항상 적용을 고려하세요**
- **모든 기능은 5계층 테스트를 완전히 구현하세요**
- **각 도메인은 독립적인 예외 처리 체계를 유지하세요**
- **새 도메인 추가 시 반드시 도메인별 예외 구조를 생성하세요**
