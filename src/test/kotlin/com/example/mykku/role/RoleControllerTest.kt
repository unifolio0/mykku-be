package com.example.mykku.role

import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.dto.MemberRoleResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RoleController::class)
class RoleControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var roleService: RoleService

    private lateinit var member: Member
    private lateinit var role: Role

    @BeforeEach
    fun setUp() {
        role = Role(id = 1L, name = "테스트 칭호", description = "설명")
        member = Member.createEmailMember(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            nickname = "테스터",
            defaultRole = role
        )
    }

    @Test
    @WithMockUser
    fun `내 칭호 목록을 조회할 수 있다`() {
        val role2 = Role(id = 2L, name = "다른 칭호", description = "")
        val memberRole1 = MemberRole(id = 1L, member = member, role = role)
        val memberRole2 = MemberRole(id = 2L, member = member, role = role2)
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
            .andExpect(jsonPath("$.data.length()").value(2))
    }

    @Test
    @WithMockUser
    fun `대표 칭호를 변경할 수 있다`() {
        mockMvc.perform(
            patch("/api/v1/roles/1/representative")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("대표 칭호 변경 성공"))
    }
}
