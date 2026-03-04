package com.example.mykku.role.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.role.adapter.output.persistence.entity.MemberRoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import com.example.mykku.role.adapter.output.persistence.repository.MemberRoleJpaRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

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
