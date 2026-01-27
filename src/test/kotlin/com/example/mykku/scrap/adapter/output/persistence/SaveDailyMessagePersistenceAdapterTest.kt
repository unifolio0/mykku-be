package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.dailymessage.adapter.output.persistence.entity.DailyMessageJpaEntity
import com.example.mykku.dailymessage.adapter.output.persistence.repository.DailyMessageJpaRepository
import com.example.mykku.dailymessage.exception.DailyMessageErrorCode
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import com.example.mykku.scrap.application.port.output.SaveDailyMessagePort
import com.example.mykku.scrap.domain.entity.SaveDailyMessageEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

@DisplayName("SaveDailyMessagePersistenceAdapter 통합 테스트")
class SaveDailyMessagePersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var saveDailyMessagePort: SaveDailyMessagePort

    @Autowired
    private lateinit var dailyMessageJpaRepository: DailyMessageJpaRepository

    private lateinit var savedMember: MemberJpaEntity
    private lateinit var savedDailyMessage: DailyMessageJpaEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(id = "testMember1", memberId = "testMember1")
        savedDailyMessage = dailyMessageJpaRepository.save(createDailyMessageJpaEntity())
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("하루덕담 저장을 생성하고 ID가 생성된다")
        fun `하루덕담 저장 - 정상 케이스`() {
            val saveDailyMessage = SaveDailyMessageEntity.create(
                memberId = savedMember.id,
                dailyMessageId = savedDailyMessage.id!!
            )

            val saved = saveDailyMessagePort.save(saveDailyMessage)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.dailyMessageId).isEqualTo(savedDailyMessage.id)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 저장하면 예외가 발생한다")
        fun `하루덕담 저장 - 존재하지 않는 회원`() {
            val saveDailyMessage = SaveDailyMessageEntity.create(
                memberId = "nonExistentMember",
                dailyMessageId = savedDailyMessage.id!!
            )

            assertThatThrownBy {
                saveDailyMessagePort.save(saveDailyMessage)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 하루덕담을 저장하면 예외가 발생한다")
        fun `하루덕담 저장 - 존재하지 않는 하루덕담`() {
            val saveDailyMessage = SaveDailyMessageEntity.create(
                memberId = savedMember.id,
                dailyMessageId = 999999L
            )

            assertThatThrownBy {
                saveDailyMessagePort.save(saveDailyMessage)
            }.isInstanceOf(DailyMessageException::class.java)
                .extracting("errorCode")
                .isEqualTo(DailyMessageErrorCode.DAILY_MESSAGE_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndDailyMessageId 메서드")
    inner class ExistsByMemberIdAndDailyMessageId {

        @Test
        @DisplayName("저장이 존재하면 true를 반환한다")
        fun `저장 존재 확인 - 존재함`() {
            val saveDailyMessage = SaveDailyMessageEntity.create(
                memberId = savedMember.id,
                dailyMessageId = savedDailyMessage.id!!
            )
            saveDailyMessagePort.save(saveDailyMessage)

            val exists = saveDailyMessagePort.existsByMemberIdAndDailyMessageId(savedMember.id, savedDailyMessage.id!!)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("저장이 존재하지 않으면 false를 반환한다")
        fun `저장 존재 확인 - 존재하지 않음`() {
            val exists = saveDailyMessagePort.existsByMemberIdAndDailyMessageId(savedMember.id, savedDailyMessage.id!!)

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드")
    inner class FindByMemberId {

        @Test
        @DisplayName("회원이 저장한 하루덕담 목록을 페이징 조회한다")
        fun `저장 목록 조회 - 정상 케이스`() {
            val dailyMessage2 = dailyMessageJpaRepository.save(createDailyMessageJpaEntity(LocalDate.now().plusDays(1)))
            saveDailyMessagePort.save(SaveDailyMessageEntity.create(savedMember.id, savedDailyMessage.id!!))
            saveDailyMessagePort.save(SaveDailyMessageEntity.create(savedMember.id, dailyMessage2.id!!))
            val pageable = PageRequest.of(0, 10)

            val page = saveDailyMessagePort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).hasSize(2)
        }

        @Test
        @DisplayName("저장한 하루덕담이 없으면 빈 페이지를 반환한다")
        fun `저장 목록 조회 - 저장 없음`() {
            val pageable = PageRequest.of(0, 10)

            val page = saveDailyMessagePort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).isEmpty()
        }

        @Test
        @DisplayName("페이징으로 일부만 조회한다")
        fun `저장 목록 조회 - 페이징`() {
            val dailyMessage2 = dailyMessageJpaRepository.save(createDailyMessageJpaEntity(LocalDate.now().plusDays(1)))
            val dailyMessage3 = dailyMessageJpaRepository.save(createDailyMessageJpaEntity(LocalDate.now().plusDays(2)))
            saveDailyMessagePort.save(SaveDailyMessageEntity.create(savedMember.id, savedDailyMessage.id!!))
            saveDailyMessagePort.save(SaveDailyMessageEntity.create(savedMember.id, dailyMessage2.id!!))
            saveDailyMessagePort.save(SaveDailyMessageEntity.create(savedMember.id, dailyMessage3.id!!))
            val pageable = PageRequest.of(0, 2)

            val page = saveDailyMessagePort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).hasSize(2)
            assertThat(page.totalElements).isEqualTo(3)
        }
    }

    @Nested
    @DisplayName("deleteByMemberIdAndDailyMessageId 메서드")
    inner class DeleteByMemberIdAndDailyMessageId {

        @Test
        @DisplayName("저장을 삭제한다")
        fun `저장 삭제 - 정상 케이스`() {
            val saveDailyMessage = SaveDailyMessageEntity.create(
                memberId = savedMember.id,
                dailyMessageId = savedDailyMessage.id!!
            )
            saveDailyMessagePort.save(saveDailyMessage)

            saveDailyMessagePort.deleteByMemberIdAndDailyMessageId(savedMember.id, savedDailyMessage.id!!)

            val exists = saveDailyMessagePort.existsByMemberIdAndDailyMessageId(savedMember.id, savedDailyMessage.id!!)
            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 저장을 삭제해도 예외가 발생하지 않는다")
        fun `저장 삭제 - 존재하지 않는 저장`() {
            saveDailyMessagePort.deleteByMemberIdAndDailyMessageId(savedMember.id, savedDailyMessage.id!!)
        }
    }

    private fun createDailyMessageJpaEntity(date: LocalDate = LocalDate.now()): DailyMessageJpaEntity {
        return DailyMessageJpaEntity(
            title = "테스트 하루덕담",
            content = "테스트 내용",
            date = date
        )
    }
}
