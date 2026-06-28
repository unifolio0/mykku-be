package com.example.mykku.achievement.application.usecase

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.achievement.application.port.input.AwardTitlesUseCase
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.role.application.port.output.MemberRoleRepository
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.entity.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("AwardTitlesService 통합 테스트")
class AwardTitlesServiceTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var awardTitlesUseCase: AwardTitlesUseCase

    @Autowired
    private lateinit var memberRoleRepository: MemberRoleRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    private fun ensureRole(name: String) {
        if (roleRepository.findByName(name) == null) {
            roleRepository.save(Role.create(name = name, description = name))
        }
    }

    private fun earnedTitleNames(memberId: Long): List<String> {
        return memberRoleRepository.findByMemberIdWithRole(memberId).map { it.role.name }
    }

    @Test
    @DisplayName("피드 업로드 1회면 첫 번째 칭호를 부여한다")
    fun awardsFirstTitleOnFirstActivity() {
        ensureRole("이 몸 등장")
        val member = createAndSaveMember(email = "award_first@test.com", socialId = "award_first")

        awardTitlesUseCase.handleActivity(member.id, ActivityType.FEED_UPLOAD)

        assertThat(earnedTitleNames(member.id)).containsExactly("이 몸 등장")
    }

    @Test
    @DisplayName("임계치 미만 반복 활동에서는 이미 보유한 칭호를 중복 부여하지 않는다")
    fun doesNotAwardDuplicateBelowNextThreshold() {
        ensureRole("이 몸 등장")
        ensureRole("영역전개")
        val member = createAndSaveMember(email = "award_dup@test.com", socialId = "award_dup")

        repeat(3) { awardTitlesUseCase.handleActivity(member.id, ActivityType.FEED_UPLOAD) }

        assertThat(earnedTitleNames(member.id)).containsExactly("이 몸 등장")
    }

    @Test
    @DisplayName("누적 카운트가 다음 임계치를 넘으면 추가 칭호를 부여한다")
    fun awardsNextTierWhenThresholdCrossed() {
        ensureRole("이 몸 등장")
        ensureRole("영역전개")
        val member = createAndSaveMember(email = "award_next@test.com", socialId = "award_next")

        repeat(5) { awardTitlesUseCase.handleActivity(member.id, ActivityType.FEED_UPLOAD) }

        assertThat(earnedTitleNames(member.id)).containsExactlyInAnyOrder("이 몸 등장", "영역전개")
    }

    @Test
    @DisplayName("대표 칭호가 없던 멤버는 첫 부여 칭호가 대표로 설정된다")
    fun setsRepresentativeWhenNone() {
        ensureRole("첫 만남")
        val member = createAndSaveMember(email = "award_rep@test.com", socialId = "award_rep")
        assertThat(member.role).isNull()

        awardTitlesUseCase.handleActivity(member.id, ActivityType.FIRST_LOGIN)

        val reloaded = memberRepository.findById(MemberPk.of(member.id))
        assertThat(reloaded!!.roleId).isNotNull()
        assertThat(earnedTitleNames(member.id)).containsExactly("첫 만남")
    }
}
