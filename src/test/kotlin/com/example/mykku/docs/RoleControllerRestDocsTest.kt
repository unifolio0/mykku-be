package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import com.example.mykku.role.RoleController
import com.example.mykku.role.RoleService
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.dto.MemberRoleResponse
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

class RoleControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var roleService: RoleService

    @InjectMocks
    private lateinit var roleController: RoleController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(roleController)
    }

    @Test
    fun `내 칭호 목록 조회 API 문서화`() {
        val role1 = Role(id = 1L, name = "신입 덕후", description = "처음 가입한 회원")
        val role2 = Role(id = 2L, name = "열정 덕후", description = "활동이 활발한 회원")
        val member = Member.createEmailMember(
            id = "member-123",
            email = "test@example.com",
            password = "password",
            nickname = "덕후왕",
        )

        val memberRole1 = MemberRole(id = 1L, member = member, role = role1).apply {
            val field = BaseEntity::class.java.getDeclaredField("createdAt")
            field.isAccessible = true
            field.set(this, LocalDateTime.now())
        }
        val memberRole2 = MemberRole(id = 2L, member = member, role = role2).apply {
            val field = BaseEntity::class.java.getDeclaredField("createdAt")
            field.isAccessible = true
            field.set(this, LocalDateTime.now())
        }
        val memberRoles = listOf(
            MemberRoleResponse(memberRole1, member),
            MemberRoleResponse(memberRole2, member)
        )

        whenever(roleService.getMyRoles(any<Member>())).thenReturn(memberRoles)

        mockMvc.perform(
            get("/api/v1/roles/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("내 칭호 목록 조회 성공"))
            .andExpect(jsonPath("$.data").isArray)
            .andDo(
                document(
                    "role-get-my-roles",
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("칭호 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                            .description("보유 칭호 ID"),
                        fieldWithPath("data[].role").type(JsonFieldType.OBJECT)
                            .description("칭호 정보"),
                        fieldWithPath("data[].role.id").type(JsonFieldType.NUMBER)
                            .description("칭호 ID"),
                        fieldWithPath("data[].role.name").type(JsonFieldType.STRING)
                            .description("칭호 이름"),
                        fieldWithPath("data[].role.description").type(JsonFieldType.STRING)
                            .description("칭호 설명").optional(),
                        fieldWithPath("data[].earnedAt").type(JsonFieldType.STRING)
                            .description("칭호 획득 일시"),
                        fieldWithPath("data[].isRepresentative").type(JsonFieldType.BOOLEAN)
                            .description("대표 칭호 여부")
                    )
                )
            )
    }

    @Test
    fun `대표 칭호 변경 API 문서화`() {
        mockMvc.perform(
            patch("/api/v1/roles/{memberRoleId}/representative", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("대표 칭호 변경 성공"))
            .andDo(
                document(
                    "role-change-representative",
                    pathParameters(
                        parameterWithName("memberRoleId").description("대표로 설정할 보유 칭호 ID")
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
}
