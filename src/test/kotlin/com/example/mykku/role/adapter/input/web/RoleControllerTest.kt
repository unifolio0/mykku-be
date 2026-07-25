package com.example.mykku.role.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.role.adapter.output.persistence.entity.MemberRoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.repository.MemberRoleJpaRepository
import com.example.mykku.role.exception.RoleErrorCode
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@DisplayName("RoleController 통합 테스트")
class RoleControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var memberRoleJpaRepository: MemberRoleJpaRepository

    private lateinit var role1: RoleJpaEntity
    private lateinit var role2: RoleJpaEntity

    @BeforeEach
    fun setUp() {
        role1 = roleJpaRepository.save(RoleJpaEntity(name = "칭호1", description = "설명1"))
        role2 = roleJpaRepository.save(RoleJpaEntity(name = "칭호2", description = "설명2"))
    }

    @Test
    @DisplayName("내 칭호 목록을 조회할 수 있다")
    fun `getMyRoles - 내 칭호 목록을 조회한다`() {
        // given
        val member = createAndSaveMember(
            nickname = "테스터",
            role = role1
        )
        memberRoleJpaRepository.save(MemberRoleJpaEntity(memberId = member.id, role = role1))
        memberRoleJpaRepository.save(MemberRoleJpaEntity(memberId = member.id, role = role2))

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .`when`()
            .get("/api/v1/roles/me")
            .then()
            .statusCode(200)
            .body("message", equalTo("내 칭호 목록 조회 성공"))
            .body("data", hasSize<Any>(2))
            .body("data[0].role.name", notNullValue())
            .body("data[0].isRepresentative", notNullValue())
    }

    @Test
    @DisplayName("전체 칭호 목록을 조회할 수 있다")
    fun `getRoles - 전체 칭호 목록을 조회한다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터")

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .`when`()
            .get("/api/v1/roles")
            .then()
            .statusCode(200)
            .body("message", equalTo("칭호 목록 조회 성공"))
            .body("data.name", hasItems("칭호1", "칭호2"))
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 전체 칭호 목록을 조회할 수 없다")
    fun `getRoles - 인증되지 않은 사용자는 401을 반환한다`() {
        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/v1/roles")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("칭호를 획득할 수 있다")
    fun `acquireRole - 칭호를 획득한다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터", role = role1)
        memberRoleJpaRepository.save(MemberRoleJpaEntity(memberId = member.id, role = role1))

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .body(AcquireRoleRequest(roleId = role2.id!!))
            .`when`()
            .post("/api/v1/roles/acquire")
            .then()
            .statusCode(200)
            .body("message", equalTo("칭호 획득 성공"))
            .body("data.acquired", equalTo(true))
            .body("data.memberRole.role.id", equalTo(role2.id!!.toInt()))
            .body("data.memberRole.role.name", equalTo("칭호2"))
            .body("data.memberRole.isRepresentative", equalTo(false))

        assertThat(memberRoleJpaRepository.findByMemberId(member.id)).hasSize(2)
    }

    @Test
    @DisplayName("대표 칭호가 없던 회원은 첫 획득 칭호가 대표로 지정된다")
    fun `acquireRole - 대표 칭호가 없으면 대표로 지정한다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터", role = null)

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .body(AcquireRoleRequest(roleId = role1.id!!))
            .`when`()
            .post("/api/v1/roles/acquire")
            .then()
            .statusCode(200)
            .body("data.acquired", equalTo(true))
            .body("data.memberRole.isRepresentative", equalTo(true))

        assertThat(memberJpaRepository.findById(member.id).get().role?.id).isEqualTo(role1.id)
    }

    @Test
    @DisplayName("이미 보유한 칭호를 다시 획득하면 멱등하게 처리된다")
    fun `acquireRole - 이미 보유한 칭호는 멱등 처리한다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터", role = role1)
        memberRoleJpaRepository.save(MemberRoleJpaEntity(memberId = member.id, role = role1))

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .body(AcquireRoleRequest(roleId = role1.id!!))
            .`when`()
            .post("/api/v1/roles/acquire")
            .then()
            .statusCode(200)
            .body("message", equalTo("이미 보유한 칭호입니다"))
            .body("data.acquired", equalTo(false))
            .body("data.memberRole.role.id", equalTo(role1.id!!.toInt()))
            .body("data.memberRole.isRepresentative", equalTo(true))

        assertThat(memberRoleJpaRepository.findByMemberId(member.id)).hasSize(1)
    }

    @Test
    @DisplayName("이미 보유했지만 대표 칭호가 비어 있으면 대표 칭호를 보정한다")
    fun `acquireRole - 이미 보유한 칭호로도 비어 있는 대표 칭호를 보정한다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터", role = null)
        memberRoleJpaRepository.save(MemberRoleJpaEntity(memberId = member.id, role = role1))

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .body(AcquireRoleRequest(roleId = role1.id!!))
            .`when`()
            .post("/api/v1/roles/acquire")
            .then()
            .statusCode(200)
            .body("data.acquired", equalTo(false))
            .body("data.memberRole.isRepresentative", equalTo(true))

        assertThat(memberJpaRepository.findById(member.id).get().role?.id).isEqualTo(role1.id)
        assertThat(memberRoleJpaRepository.findByMemberId(member.id)).hasSize(1)
    }

    @Test
    @DisplayName("roleId가 없거나 양수가 아니면 400을 반환한다")
    fun `acquireRole - 잘못된 roleId는 400을 반환한다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터")
        val invalidBodies = listOf("{}", """{"roleId":null}""", """{"roleId":0}""", """{"roleId":-5}""")

        // when & then
        invalidBodies.forEach { body ->
            RestAssured
                .given()
                .contentType(ContentType.JSON)
                .headers(createAuthHeaders(member.id))
                .body(body)
                .`when`()
                .post("/api/v1/roles/acquire")
                .then()
                .statusCode(400)
        }
    }

    @Test
    @DisplayName("정수가 아닌 roleId는 절삭되지 않고 400을 반환한다")
    fun `acquireRole - 소수점 roleId는 400을 반환한다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터", role = null)

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .body("""{"roleId":${role1.id!!}.9}""")
            .`when`()
            .post("/api/v1/roles/acquire")
            .then()
            .statusCode(400)

        assertThat(memberRoleJpaRepository.findByMemberId(member.id)).isEmpty()
    }

    @Test
    @DisplayName("같은 칭호를 동시에 획득해도 정확히 한 요청만 신규 획득으로 응답한다")
    fun `acquireRole - 동일 칭호 동시 요청은 중복 없이 처리된다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터", role = null)

        // when
        val responses = acquireConcurrently(member.id, List(8) { role1.id!! })

        // then
        assertThat(responses).allMatch { it.status == 200 }
        assertThat(responses.count { it.acquired }).isEqualTo(1)
        assertThat(memberRoleJpaRepository.findByMemberId(member.id)).hasSize(1)
        assertThat(memberJpaRepository.findById(member.id).get().role?.id).isEqualTo(role1.id)
    }

    @Test
    @DisplayName("서로 다른 칭호를 동시에 획득해도 데드락 없이 모두 부여된다")
    fun `acquireRole - 서로 다른 칭호 동시 요청은 데드락 없이 처리된다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터", role = null)
        val role3 = roleJpaRepository.save(RoleJpaEntity(name = "칭호3", description = "설명3"))
        val role4 = roleJpaRepository.save(RoleJpaEntity(name = "칭호4", description = "설명4"))
        val targets = listOf(role1.id!!, role2.id!!, role3.id!!, role4.id!!)

        // when
        val responses = acquireConcurrently(member.id, targets + targets)

        // then
        assertThat(responses).allMatch { it.status == 200 }
        assertThat(responses.count { it.acquired }).isEqualTo(targets.size)
        assertThat(responses.count { it.isRepresentative }).isEqualTo(1)
        assertThat(memberRoleJpaRepository.findByMemberId(member.id)).hasSize(4)
        assertThat(memberJpaRepository.findById(member.id).get().role?.id).isIn(targets)
    }

    private data class AcquireOutcome(val status: Int, val acquired: Boolean, val isRepresentative: Boolean)

    private fun acquireConcurrently(memberPk: Long, roleIds: List<Long>): List<AcquireOutcome> {
        val executor = Executors.newFixedThreadPool(roleIds.size)
        val start = CountDownLatch(1)
        try {
            val futures = roleIds.map { roleId ->
                executor.submit<AcquireOutcome> { start.await(); requestAcquire(memberPk, roleId) }
            }
            start.countDown()
            return futures.map { it.get(30, TimeUnit.SECONDS) }
        } finally {
            executor.shutdownNow()
        }
    }

    private fun requestAcquire(memberPk: Long, roleId: Long): AcquireOutcome {
        val response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(memberPk))
            .body(AcquireRoleRequest(roleId = roleId))
            .`when`()
            .post("/api/v1/roles/acquire")
            .then()
            .extract()

        if (response.statusCode() != 200) return AcquireOutcome(response.statusCode(), false, false)
        return AcquireOutcome(
            status = response.statusCode(),
            acquired = response.path("data.acquired"),
            isRepresentative = response.path("data.memberRole.isRepresentative")
        )
    }

    @Test
    @DisplayName("존재하지 않는 칭호는 획득할 수 없다")
    fun `acquireRole - 존재하지 않는 칭호는 404를 반환한다`() {
        // given
        val member = createAndSaveMember(nickname = "테스터")

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .body(AcquireRoleRequest(roleId = 999999L))
            .`when`()
            .post("/api/v1/roles/acquire")
            .then()
            .statusCode(404)
            .body("code", equalTo(RoleErrorCode.ROLE_NOT_FOUND.code))
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 칭호를 획득할 수 없다")
    fun `acquireRole - 인증되지 않은 사용자는 401을 반환한다`() {
        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(AcquireRoleRequest(roleId = role1.id!!))
            .`when`()
            .post("/api/v1/roles/acquire")
            .then()
            .statusCode(401)
    }

    @Test
    @DisplayName("대표 칭호를 변경할 수 있다")
    fun `changeRepresentativeRole - 대표 칭호를 변경한다`() {
        // given
        val member = createAndSaveMember(
            nickname = "테스터",
            role = role1
        )
        val memberRole1 = memberRoleJpaRepository.save(MemberRoleJpaEntity(memberId = member.id, role = role1))
        val memberRole2 = memberRoleJpaRepository.save(MemberRoleJpaEntity(memberId = member.id, role = role2))

        // when & then
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .headers(createAuthHeaders(member.id))
            .`when`()
            .patch("/api/v1/roles/${memberRole2.id}/representative")
            .then()
            .statusCode(200)
            .body("message", equalTo("대표 칭호 변경 성공"))
    }
}
