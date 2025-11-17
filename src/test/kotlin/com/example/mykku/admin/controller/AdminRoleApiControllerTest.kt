package com.example.mykku.admin.controller

import com.example.mykku.admin.service.AdminRoleService
import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.dto.CreateRoleRequest
import com.example.mykku.role.dto.MemberRoleResponse
import com.example.mykku.role.dto.RoleResponse
import com.example.mykku.role.dto.UpdateRoleRequest
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AdminRoleApiController::class)
class AdminRoleApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var adminRoleService: AdminRoleService

    private lateinit var role: Role

    @BeforeEach
    fun setUp() {
        role = Role(id = 1L, name = "테스트 칭호", description = "테스트 설명")
    }

    @Test
    @WithMockUser
    fun `모든 칭호를 조회할 수 있다`() {
        val roles = listOf(
            RoleResponse(Role(id = 1L, name = "칭호1", description = "설명1")),
            RoleResponse(Role(id = 2L, name = "칭호2", description = "설명2"))
        )
        whenever(adminRoleService.getAllRoles()).thenReturn(roles)

        mockMvc.perform(
            get("/admin/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("칭호 목록 조회 성공"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
    }

    @Test
    @WithMockUser
    fun `새로운 칭호를 생성할 수 있다`() {
        val request = CreateRoleRequest(name = "새 칭호", description = "새 설명")
        val response = RoleResponse(role)
        whenever(adminRoleService.createRole(any<CreateRoleRequest>())).thenReturn(response)

        mockMvc.perform(
            post("/admin/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("칭호 생성 성공"))
            .andExpect(jsonPath("$.data.name").value("테스트 칭호"))
    }

    @Test
    @WithMockUser
    fun `칭호를 수정할 수 있다`() {
        val request = UpdateRoleRequest(name = "수정된 칭호", description = "수정된 설명")
        val response = RoleResponse(role)
        whenever(adminRoleService.updateRole(any<Long>(), any<UpdateRoleRequest>())).thenReturn(response)

        mockMvc.perform(
            put("/admin/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("칭호 수정 성공"))
    }

    @Test
    @WithMockUser
    fun `칭호를 삭제할 수 있다`() {
        mockMvc.perform(
            delete("/admin/api/v1/roles/1")
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("칭호 삭제 성공"))
    }

    @Test
    @WithMockUser
    fun `회원에게 칭호를 부여할 수 있다`() {
        val member = Member.createEmailMember(
            id = "test-id",
            email = "test@example.com",
            password = "password",
            nickname = "테스터",
            defaultRole = role
        )
        val memberRole = MemberRole(id = 1L, member = member, role = role)
        val response = MemberRoleResponse(memberRole, member)
        whenever(adminRoleService.assignRoleToMember(any<Long>(), any<String>())).thenReturn(response)

        mockMvc.perform(
            post("/admin/api/v1/roles/1/members/test-id")
                .with(csrf())
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("칭호 부여 성공"))
    }
}
