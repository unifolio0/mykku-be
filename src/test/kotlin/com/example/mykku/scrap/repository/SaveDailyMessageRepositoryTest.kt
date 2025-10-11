package com.example.mykku.scrap.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.repository.DailyMessageRepository
import com.example.mykku.scrap.domain.SaveDailyMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

@DisplayName("SaveDailyMessageRepository 테스트")
class SaveDailyMessageRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var saveDailyMessageRepository: SaveDailyMessageRepository

    @Autowired
    private lateinit var dailyMessageRepository: DailyMessageRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("하루덕담을 저장하고 조회한다")
    fun `하루덕담을 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val dailyMessage = dailyMessageRepository.save(
            DailyMessage(
                title = "테스트 덕담",
                content = "응원합니다!",
                date = LocalDate.now()
            )
        )

        val saveDailyMessage = SaveDailyMessage(
            member = member,
            dailyMessage = dailyMessage
        )

        // when
        val saved = saveDailyMessageRepository.save(saveDailyMessage)
        testEntityManager.flush()
        testEntityManager.clear()

        val found = saveDailyMessageRepository.findById(saved.id!!).orElse(null)

        // then
        assertThat(found).isNotNull
        assertThat(found.member.id).isEqualTo(member.id)
        assertThat(found.dailyMessage.id).isEqualTo(dailyMessage.id)
    }

    @Test
    @DisplayName("회원과 하루덕담으로 저장 여부를 확인한다")
    fun `회원과 하루덕담으로 저장 여부를 확인한다`() {
        // given
        val member = createAndSaveMember()
        val dailyMessage = dailyMessageRepository.save(
            DailyMessage(title = "덕담", content = "파이팅!", date = LocalDate.now())
        )

        saveDailyMessageRepository.save(SaveDailyMessage(member = member, dailyMessage = dailyMessage))
        testEntityManager.flush()

        // when
        val exists = saveDailyMessageRepository.existsByMemberAndDailyMessage(member, dailyMessage)

        // then
        assertThat(exists).isTrue()
    }

    @Test
    @DisplayName("회원의 저장된 하루덕담을 페이지네이션으로 조회한다")
    fun `회원의 저장된 하루덕담을 페이지네이션으로 조회한다`() {
        // given
        val member = createAndSaveMember()
        for (i in 1..5) {
            val dailyMessage = dailyMessageRepository.save(
                DailyMessage(title = "덕담$i", content = "내용$i", date = LocalDate.now().plusDays(i.toLong()))
            )
            saveDailyMessageRepository.save(SaveDailyMessage(member = member, dailyMessage = dailyMessage))
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val page = saveDailyMessageRepository.findByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(5)
        assertThat(page.totalElements).isEqualTo(5)
    }

    @Test
    @DisplayName("회원과 하루덕담으로 저장을 삭제한다")
    fun `회원과 하루덕담으로 저장을 삭제한다`() {
        // given
        val member = createAndSaveMember()
        val dailyMessage = dailyMessageRepository.save(
            DailyMessage(title = "삭제할 덕담", content = "테스트", date = LocalDate.now())
        )

        saveDailyMessageRepository.save(SaveDailyMessage(member = member, dailyMessage = dailyMessage))
        testEntityManager.flush()

        // when
        saveDailyMessageRepository.deleteByMemberAndDailyMessage(member, dailyMessage)
        testEntityManager.flush()

        // then
        val exists = saveDailyMessageRepository.existsByMemberAndDailyMessage(member, dailyMessage)
        assertThat(exists).isFalse()
    }
}
