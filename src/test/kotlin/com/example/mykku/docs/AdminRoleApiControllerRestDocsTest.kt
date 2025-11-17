package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.admin.controller.AdminRoleApiController
import com.example.mykku.admin.service.AdminRoleService
import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.dto.CreateRoleRequest
import com.example.mykku.role.dto.MemberRoleResponse
import com.example.mykku.role.dto.RoleResponse
import com.example.mykku.role.dto.UpdateRoleRequest
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

class AdminRoleApiControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var adminRoleService: AdminRoleService

    @InjectMocks
    private lateinit var adminRoleApiController: AdminRoleApiController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(adminRoleApiController)
    }

    @Test
    fun `칭호 목록 조회 API 문서화`() {
        val roles = listOf(
            RoleResponse(Role(id = 1L, name = "신입 덕후", description = "처음 가입한 회원")),
            RoleResponse(Role(id = 2L, name = "열정 덕후", description = "활동이 활발한 회원")),
            RoleResponse(Role(id = 3L, name = "베테랑 덕후", description = "오래된 회원"))
        )

        whenever(adminRoleService.getAllRoles()).thenReturn(roles)

        mockMvc.perform(
            get("/admin/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("칭호 목록 조회 성공"))
            .andDo(
                document(
                    "admin-role-get-all",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("칭호 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                            .description("칭호 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING)
                            .description("칭호 이름"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING)
                            .description("칭호 설명").optional()
                    )
                )
            )
    }

    @Test
    fun `칭호 생성 API 문서화`() {
        val request = CreateRoleRequest(
            name = "슈퍼 덕후",
            description = "최고 등급 회원"
        )
        val response = RoleResponse(
            Role(id = 1L, name = "슈퍼 덕후", description = "최고 등급 회원")
        )

        whenever(adminRoleService.createRole(any<CreateRoleRequest>())).thenReturn(response)

        mockMvc.perform(
            post("/admin/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("칭호 생성 성공"))
            .andDo(
                document(
                    "admin-role-create",
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("칭호 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("칭호 설명").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("생성된 칭호 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                            .description("칭호 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING)
                            .description("칭호 이름"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING)
                            .description("칭호 설명").optional()
                    )
                )
            )
    }

    @Test
    fun `칭호 수정 API 문서화`() {
        val request = UpdateRoleRequest(
            name = "수정된 덕후",
            description = "수정된 설명"
        )
        val response = RoleResponse(
            Role(id = 1L, name = "수정된 덕후", description = "수정된 설명")
        )

        whenever(adminRoleService.updateRole(any<Long>(), any<UpdateRoleRequest>())).thenReturn(response)

        mockMvc.perform(
            put("/admin/api/v1/roles/{roleId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("칭호 수정 성공"))
            .andDo(
                document(
                    "admin-role-update",
                    pathParameters(
                        parameterWithName("roleId").description("수정할 칭호 ID")
                    ),
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("칭호 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("칭호 설명").optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("수정된 칭호 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                            .description("칭호 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING)
                            .description("칭호 이름"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING)
                            .description("칭호 설명").optional()
                    )
                )
            )
    }

    @Test
    fun `칭호 삭제 API 문서화`() {
        mockMvc.perform(
            delete("/admin/api/v1/roles/{roleId}", 1L)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("칭호 삭제 성공"))
            .andDo(
                document(
                    "admin-role-delete",
                    pathParameters(
                        parameterWithName("roleId").description("삭제할 칭호 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 데이터 (없음)").optional()
                    )
                )
            )
    }

    @Test
    fun `회원에게 칭호 부여 API 문서화`() {
        val role = Role(id = 1L, name = "열정 덕후", description = "활동이 활발한 회원")
        val member = Member.createEmailMember(
            id = "member-123",
            email = "test@example.com",
            password = "password",
            nickname = "덕후왕",
        )
        val memberRole = MemberRole(id = 1L, member = member, role = role).apply {
            val field = BaseEntity::class.java.getDeclaredField("createdAt")
            field.isAccessible = true
            field.set(this, LocalDateTime.now())
        }
        val response = MemberRoleResponse(memberRole, member)

        whenever(adminRoleService.assignRoleToMember(any<Long>(), any<String>())).thenReturn(response)

        mockMvc.perform(
            post("/admin/api/v1/roles/{roleId}/members/{memberId}", 1L, "member-123")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("칭호 부여 성공"))
            .andDo(
                document(
                    "admin-role-assign",
                    pathParameters(
                        parameterWithName("roleId").description("부여할 칭호 ID"),
                        parameterWithName("memberId").description("칭호를 부여받을 회원 ID")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("부여된 칭호 정보"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                            .description("보유 칭호 ID"),
                        fieldWithPath("data.role").type(JsonFieldType.OBJECT)
                            .description("칭호 정보"),
                        fieldWithPath("data.role.id").type(JsonFieldType.NUMBER)
                            .description("칭호 ID"),
                        fieldWithPath("data.role.name").type(JsonFieldType.STRING)
                            .description("칭호 이름"),
                        fieldWithPath("data.role.description").type(JsonFieldType.STRING)
                            .description("칭호 설명").optional(),
                        fieldWithPath("data.earnedAt").type(JsonFieldType.STRING)
                            .description("칭호 부여 일시"),
                        fieldWithPath("data.isRepresentative").type(JsonFieldType.BOOLEAN)
                            .description("대표 칭호 여부")
                    )
                )
            )
    }
}
