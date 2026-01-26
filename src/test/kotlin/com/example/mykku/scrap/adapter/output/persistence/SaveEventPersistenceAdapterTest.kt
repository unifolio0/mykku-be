package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.event.exception.EventErrorCode
import com.example.mykku.event.exception.EventException
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import com.example.mykku.scrap.application.port.output.SaveEventPort
import com.example.mykku.scrap.domain.entity.SaveEventEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("SaveEventPersistenceAdapter 통합 테스트")
class SaveEventPersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var saveEventPort: SaveEventPort

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    private lateinit var savedMember: MemberJpaEntity
    private lateinit var savedEvent: EventJpaEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(id = "testMember1", memberId = "testMember1")
        savedEvent = eventJpaRepository.save(createEventJpaEntity())
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("이벤트 저장을 생성하고 ID가 생성된다")
        fun `이벤트 저장 - 정상 케이스`() {
            val saveEvent = SaveEventEntity.create(
                memberId = savedMember.id,
                eventId = savedEvent.id!!
            )

            val saved = saveEventPort.save(saveEvent)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.eventId).isEqualTo(savedEvent.id)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 저장하면 예외가 발생한다")
        fun `이벤트 저장 - 존재하지 않는 회원`() {
            val saveEvent = SaveEventEntity.create(
                memberId = "nonExistentMember",
                eventId = savedEvent.id!!
            )

            assertThatThrownBy {
                saveEventPort.save(saveEvent)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 이벤트를 저장하면 예외가 발생한다")
        fun `이벤트 저장 - 존재하지 않는 이벤트`() {
            val saveEvent = SaveEventEntity.create(
                memberId = savedMember.id,
                eventId = 999999L
            )

            assertThatThrownBy {
                saveEventPort.save(saveEvent)
            }.isInstanceOf(EventException::class.java)
                .extracting("errorCode")
                .isEqualTo(EventErrorCode.EVENT_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndEventId 메서드")
    inner class ExistsByMemberIdAndEventId {

        @Test
        @DisplayName("저장이 존재하면 true를 반환한다")
        fun `저장 존재 확인 - 존재함`() {
            val saveEvent = SaveEventEntity.create(
                memberId = savedMember.id,
                eventId = savedEvent.id!!
            )
            saveEventPort.save(saveEvent)

            val exists = saveEventPort.existsByMemberIdAndEventId(savedMember.id, savedEvent.id!!)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("저장이 존재하지 않으면 false를 반환한다")
        fun `저장 존재 확인 - 존재하지 않음`() {
            val exists = saveEventPort.existsByMemberIdAndEventId(savedMember.id, savedEvent.id!!)

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드")
    inner class FindByMemberId {

        @Test
        @DisplayName("회원이 저장한 이벤트 목록을 페이징 조회한다")
        fun `저장 목록 조회 - 정상 케이스`() {
            val event2 = eventJpaRepository.save(createEventJpaEntity("이벤트2"))
            saveEventPort.save(SaveEventEntity.create(savedMember.id, savedEvent.id!!))
            saveEventPort.save(SaveEventEntity.create(savedMember.id, event2.id!!))
            val pageable = PageRequest.of(0, 10)

            val page = saveEventPort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).hasSize(2)
        }

        @Test
        @DisplayName("저장한 이벤트가 없으면 빈 페이지를 반환한다")
        fun `저장 목록 조회 - 저장 없음`() {
            val pageable = PageRequest.of(0, 10)

            val page = saveEventPort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).isEmpty()
        }

        @Test
        @DisplayName("페이징으로 일부만 조회한다")
        fun `저장 목록 조회 - 페이징`() {
            val event2 = eventJpaRepository.save(createEventJpaEntity("이벤트2"))
            val event3 = eventJpaRepository.save(createEventJpaEntity("이벤트3"))
            saveEventPort.save(SaveEventEntity.create(savedMember.id, savedEvent.id!!))
            saveEventPort.save(SaveEventEntity.create(savedMember.id, event2.id!!))
            saveEventPort.save(SaveEventEntity.create(savedMember.id, event3.id!!))
            val pageable = PageRequest.of(0, 2)

            val page = saveEventPort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).hasSize(2)
            assertThat(page.totalElements).isEqualTo(3)
        }
    }

    @Nested
    @DisplayName("deleteByMemberIdAndEventId 메서드")
    inner class DeleteByMemberIdAndEventId {

        @Test
        @DisplayName("저장을 삭제한다")
        fun `저장 삭제 - 정상 케이스`() {
            val saveEvent = SaveEventEntity.create(
                memberId = savedMember.id,
                eventId = savedEvent.id!!
            )
            saveEventPort.save(saveEvent)

            saveEventPort.deleteByMemberIdAndEventId(savedMember.id, savedEvent.id!!)

            val exists = saveEventPort.existsByMemberIdAndEventId(savedMember.id, savedEvent.id!!)
            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 저장을 삭제해도 예외가 발생하지 않는다")
        fun `저장 삭제 - 존재하지 않는 저장`() {
            saveEventPort.deleteByMemberIdAndEventId(savedMember.id, savedEvent.id!!)
        }
    }

    @Nested
    @DisplayName("findByMemberIdAndEventIdIn 메서드")
    inner class FindByMemberIdAndEventIdIn {

        @Test
        @DisplayName("회원이 저장한 이벤트 목록을 조회한다")
        fun `저장 목록 조회 - 정상 케이스`() {
            val event2 = eventJpaRepository.save(createEventJpaEntity("이벤트2"))
            saveEventPort.save(SaveEventEntity.create(savedMember.id, savedEvent.id!!))
            saveEventPort.save(SaveEventEntity.create(savedMember.id, event2.id!!))

            val saves = saveEventPort.findByMemberIdAndEventIdIn(
                savedMember.id,
                listOf(savedEvent.id!!, event2.id!!)
            )

            assertThat(saves).hasSize(2)
        }

        @Test
        @DisplayName("빈 이벤트 ID 목록으로 조회하면 빈 리스트를 반환한다")
        fun `저장 목록 조회 - 빈 목록`() {
            val saves = saveEventPort.findByMemberIdAndEventIdIn(savedMember.id, emptyList())

            assertThat(saves).isEmpty()
        }

        @Test
        @DisplayName("저장하지 않은 이벤트 ID로 조회하면 빈 리스트를 반환한다")
        fun `저장 목록 조회 - 저장하지 않은 이벤트`() {
            val saves = saveEventPort.findByMemberIdAndEventIdIn(
                savedMember.id,
                listOf(savedEvent.id!!)
            )

            assertThat(saves).isEmpty()
        }
    }

    private fun createEventJpaEntity(title: String = "테스트 이벤트"): EventJpaEntity {
        return EventJpaEntity(
            title = title,
            description = "테스트 이벤트 설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7),
            scrapCount = 0,
            status = EventStatusType.ACTIVE
        )
    }
}
