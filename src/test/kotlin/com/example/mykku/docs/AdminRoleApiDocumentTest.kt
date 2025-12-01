package com.example.mykku.docs

import com.example.mykku.role.dto.CreateRoleRequest
import com.example.mykku.role.dto.MemberRoleResponse
import com.example.mykku.role.dto.RoleResponse
import com.example.mykku.role.dto.UpdateRoleRequest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import java.time.LocalDateTime

class AdminRoleApiDocumentTest : BaseDocumentTest() {

    private lateinit var adminSessionId: String

    @BeforeEach
    override fun setEnvironment(restDocumentation: RestDocumentationContextProvider) {
        super.setEnvironment(restDocumentation)
        adminSessionId = getAdminSessionId()
    }

    private fun getAdminSessionId(): String {
        return RestAssured
            .given()
            .formParam("token", "test-admin-token")
            .redirects().follow(false)
            .`when`()
            .post("/admin/api/login")
            .then()
            .statusCode(302)
            .extract()
            .sessionId()
    }

    @Test
    fun `칭호 목록 조회`() {
        val roles = listOf(
            RoleResponse(id = 1L, name = "신입 덕후", description = "처음 가입한 회원"),
            RoleResponse(id = 2L, name = "열정 덕후", description = "활동이 활발한 회원"),
            RoleResponse(id = 3L, name = "베테랑 덕후", description = "오래된 회원")
        )

        `when`(adminRoleService.getAllRoles()).thenReturn(roles)

        val documentFilter = document("admin/role-list", 200)
            .request(
                request()
                    .tag(Tag.ADMIN_ROLE_API)
                    .summary("칭호 목록 조회")
                    .description("모든 칭호 목록을 조회합니다.")
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("칭호 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("칭호 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("칭호 이름"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("칭호 설명").optional()
                    )
            )
            .build()

        given(documentFilter)
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .`when`()
            .get("/admin/api/v1/roles")
            .then()
            .statusCode(200)
    }

    @Test
    fun `칭호 생성`() {
        val request = CreateRoleRequest(
            name = "슈퍼 덕후",
            description = "최고 등급 회원"
        )
        val response = RoleResponse(
            id = 1L,
            name = "슈퍼 덕후",
            description = "최고 등급 회원"
        )

        `when`(adminRoleService.createRole(any())).thenReturn(response)

        val documentFilter = document("admin/role-create", 201)
            .request(
                request()
                    .tag(Tag.ADMIN_ROLE_API)
                    .summary("칭호 생성")
                    .description("새로운 칭호를 생성합니다.")
                    .requestBodyField(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("칭호 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("칭호 설명").optional()
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("생성된 칭호 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("칭호 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("칭호 이름"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("칭호 설명").optional()
                    )
            )
            .build()

        given(documentFilter)
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/admin/api/v1/roles")
            .then()
            .statusCode(201)
    }

    @Test
    fun `칭호 수정`() {
        val roleId = 1L
        val request = UpdateRoleRequest(
            name = "수정된 덕후",
            description = "수정된 설명"
        )
        val response = RoleResponse(
            id = roleId,
            name = "수정된 덕후",
            description = "수정된 설명"
        )

        `when`(adminRoleService.updateRole(eq(roleId), any())).thenReturn(response)

        val documentFilter = document("admin/role-update", 200)
            .request(
                request()
                    .tag(Tag.ADMIN_ROLE_API)
                    .summary("칭호 수정")
                    .description("기존 칭호를 수정합니다.")
                    .pathParameter(
                        parameterWithName("roleId").description("수정할 칭호 ID")
                    )
                    .requestBodyField(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("칭호 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("칭호 설명").optional()
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("수정된 칭호 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("칭호 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("칭호 이름"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("칭호 설명").optional()
                    )
            )
            .build()

        given(documentFilter)
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .put("/admin/api/v1/roles/{roleId}", roleId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `칭호 삭제`() {
        val roleId = 1L

        doNothing().`when`(adminRoleService).deleteRole(eq(roleId))

        val documentFilter = document("admin/role-delete", 200)
            .request(
                request()
                    .tag(Tag.ADMIN_ROLE_API)
                    .summary("칭호 삭제")
                    .description("칭호를 삭제합니다.")
                    .pathParameter(
                        parameterWithName("roleId").description("삭제할 칭호 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .`when`()
            .delete("/admin/api/v1/roles/{roleId}", roleId)
            .then()
            .statusCode(200)
    }

    @Test
    fun `회원에게 칭호 부여`() {
        val roleId = 1L
        val memberId = "member-123"
        val response = MemberRoleResponse(
            id = 1L,
            role = RoleResponse(id = roleId, name = "열정 덕후", description = "활동이 활발한 회원"),
            isRepresentative = false,
            earnedAt = LocalDateTime.now()
        )

        `when`(adminRoleService.assignRoleToMember(eq(roleId), eq(memberId))).thenReturn(response)

        val documentFilter = document("admin/role-assign", 201)
            .request(
                request()
                    .tag(Tag.ADMIN_ROLE_API)
                    .summary("회원에게 칭호 부여")
                    .description("특정 회원에게 칭호를 부여합니다.")
                    .pathParameter(
                        parameterWithName("roleId").description("부여할 칭호 ID"),
                        parameterWithName("memberId").description("칭호를 부여받을 회원 ID")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("부여된 칭호 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("보유 칭호 ID"),
                        fieldWithPath("data.role").type(JsonFieldType.OBJECT).description("칭호 정보"),
                        fieldWithPath("data.role.id").type(JsonFieldType.NUMBER).description("칭호 ID"),
                        fieldWithPath("data.role.name").type(JsonFieldType.STRING).description("칭호 이름"),
                        fieldWithPath("data.role.description").type(JsonFieldType.STRING).description("칭호 설명")
                            .optional(),
                        fieldWithPath("data.earnedAt").type(JsonFieldType.STRING).description("칭호 부여 일시"),
                        fieldWithPath("data.isRepresentative").type(JsonFieldType.BOOLEAN).description("대표 칭호 여부")
                    )
            )
            .build()

        given(documentFilter)
            .sessionId(adminSessionId)
            .contentType(ContentType.JSON)
            .`when`()
            .post("/admin/api/v1/roles/{roleId}/members/{memberId}", roleId, memberId)
            .then()
            .statusCode(201)
    }
}
