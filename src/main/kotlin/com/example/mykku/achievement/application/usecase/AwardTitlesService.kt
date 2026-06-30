package com.example.mykku.achievement.application.usecase

import com.example.mykku.achievement.application.port.input.AwardTitlesUseCase
import com.example.mykku.achievement.application.port.output.MemberActivityCountRepository
import com.example.mykku.achievement.domain.TitleThreshold
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.role.application.port.output.MemberRoleRepository
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.entity.MemberRole
import com.example.mykku.role.domain.entity.Role
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AwardTitlesService(
    private val memberActivityCountRepository: MemberActivityCountRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val roleRepository: RoleRepository,
    private val memberRepository: MemberRepository
) : AwardTitlesUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun handleActivity(memberId: Long, activityType: ActivityType) {
        val newCount = memberActivityCountRepository.incrementAndGet(memberId, activityType)
        TitleThreshold.forActivity(activityType)
            .filter { it.threshold <= newCount }
            .forEach { awardIfMissing(memberId, it.roleName) }
    }

    private fun awardIfMissing(memberId: Long, roleName: String) {
        val role = roleRepository.findByName(roleName)
        if (role == null) {
            log.warn("칭호 role을 찾을 수 없습니다: {}", roleName)
            return
        }
        if (memberRoleRepository.existsByMemberIdAndRoleId(memberId, role.id)) return
        memberRoleRepository.save(MemberRole.create(memberId, role.id))
        assignAsRepresentativeIfNone(memberId, role)
    }

    private fun assignAsRepresentativeIfNone(memberId: Long, role: Role) {
        val member = memberRepository.findById(MemberPk.of(memberId)) ?: return
        if (member.roleId != null) return
        member.assignRole(role.id.value)
        memberRepository.save(member)
    }
}
