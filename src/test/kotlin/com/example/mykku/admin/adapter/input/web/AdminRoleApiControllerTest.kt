package com.example.mykku.admin.adapter.input.web

import com.example.mykku.BaseControllerTest
import com.example.mykku.role.adapter.input.web.CreateRoleRequest
import com.example.mykku.role.adapter.input.web.UpdateRoleRequest
import com.example.mykku.role.adapter.output.persistence.entity.RoleJpaEntity
import com.example.mykku.role.application.port.output.MemberRoleRepository
import com.example.mykku.role.domain.entity.Role
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("AdminRoleApiController 통합 테스트")
class AdminRoleApiControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var memberRoleRepository: MemberRoleRepository

    private lateinit var role1: RoleJpaEntity
    private lateinit var role2: RoleJpaEntity
    private lateinit var adminSessionId: String

    @BeforeEach
    fun setUp() {
        role1 = roleJpaRepository.save(RoleJpaEntity(name = "칭호1", description = "설명1"))
        role2 = roleJpaRepository.save(RoleJpaEntity(name = "칭호2", description = "설명2"))
        adminSessionId = getAdminSessionId()
    }

    @Test
    @DisplayName("모든 칭호를 조회할 수 있다")
    fun `getAllRoles - 모든 칭호를 조회한다`() {
        // when & then
        RestAssured
            .given()
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/admin/api/v1/roles")
            .then()
            .statusCode(200)
            .body("message", equalTo("칭호 목록 조회 성공"))
            .body("data", hasSize<Any>(greaterThanOrEqualTo(2)))
    }

    @Test
    @DisplayName("새로운 칭호를 생성할 수 있다")
    fun `createRole - 새로운 칭호를 생성한다`() {
        // given
        val request = CreateRoleRequest(name = "새로운칭호", description = "새로운 설명")

        // when & then
        RestAssured
            .given()
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .post("/admin/api/v1/roles")
            .then()
            .statusCode(201)
            .body("message", equalTo("칭호 생성 성공"))
            .body("data.name", equalTo("새로운칭호"))
            .body("data.description", equalTo("새로운 설명"))
    }

    @Test
    @DisplayName("칭호를 수정할 수 있다")
    fun `updateRole - 칭호를 수정한다`() {
        // given
        val request = UpdateRoleRequest(name = "수정된칭호", description = "수정된 설명")

        // when & then
        RestAssured
            .given()
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
            .put("/admin/api/v1/roles/${role1.id}")
            .then()
            .statusCode(200)
            .body("message", equalTo("칭호 수정 성공"))
            .body("data.name", equalTo("수정된칭호"))
    }

    @Test
    @DisplayName("칭호를 삭제할 수 있다")
    fun `deleteRole - 칭호를 삭제한다`() {
        // given
        val roleToDelete = roleJpaRepository.save(RoleJpaEntity(name = "삭제될칭호", description = "설명"))

        // when & then
        RestAssured
            .given()
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .`when`()
            .delete("/admin/api/v1/roles/${roleToDelete.id}")
            .then()
            .statusCode(200)
            .body("message", equalTo("칭호 삭제 성공"))
    }

    @Test
    @DisplayName("회원에게 칭호를 부여할 수 있다")
    fun `assignRoleToMember - 회원에게 칭호를 부여한다`() {
        // given
        val member = createAndSaveMember(
            id = "test-member",
            nickname = "테스터",
            role = role1
        )

        // when & then
        RestAssured
            .given()
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .`when`()
            .post("/admin/api/v1/roles/${role2.id}/members/${member.id}")
            .then()
            .statusCode(201)
            .body("message", equalTo("칭호 부여 성공"))
            .body("data.role.name", equalTo(role2.name))
    }
}
