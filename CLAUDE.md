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
- **금지사항**: Repository나 Tool 계층에 직접 의존 불가
- **예시**:
  ```kotlin
  @RestController
  class FeedController(
      private val feedService: FeedService,  // ✅ Service 계층만 의존
      // private val feedRepository: FeedRepository  // ❌ Repository 직접 의존 금지
  )
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

### Exception Handling

- `MykkuException` 사용
- `ErrorCode` enum으로 에러 코드 관리
- GlobalExceptionHandler에서 통합 처리

### Transaction Management

- Service 계층에서 `@Transactional` 사용
- 읽기 전용 메서드는 `@Transactional(readOnly = true)` 사용

## Directory Structure

```
src/main/kotlin/com/example/mykku/
├── [domain]/
│   ├── domain/        # Entity 클래스
│   ├── dto/           # DTO 클래스
│   ├── repository/    # Repository 인터페이스
│   ├── tool/          # Tool 계층 (Reader, Writer)
│   ├── [Domain]Controller.kt
│   └── [Domain]Service.kt
├── auth/              # 인증/인가
├── common/            # 공통 클래스
├── config/            # 설정 클래스
├── exception/         # 예외 처리
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

## Important Notes

- **절대 Service에서 Repository를 직접 주입하지 마세요**
- **Controller에서는 오직 Service만 사용하세요**
- **복잡한 쿼리는 Tool 계층에 캡슐화하세요**
- **페이지네이션은 항상 적용을 고려하세요**
- **모든 기능은 5계층 테스트를 완전히 구현하세요**
