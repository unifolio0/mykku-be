package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.fannote.adapter.output.persistence.entity.FanNoteJpaEntity
import com.example.mykku.fannote.adapter.output.persistence.repository.FanNoteJpaRepository
import com.example.mykku.fannote.exception.FanNoteErrorCode
import com.example.mykku.fannote.exception.FanNoteException
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import com.example.mykku.scrap.application.port.output.SaveFanNotePort
import com.example.mykku.scrap.domain.entity.SaveFanNoteEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

@DisplayName("SaveFanNotePersistenceAdapter 통합 테스트")
class SaveFanNotePersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var saveFanNotePort: SaveFanNotePort

    @Autowired
    private lateinit var fanNoteJpaRepository: FanNoteJpaRepository

    private lateinit var savedMember: MemberJpaEntity
    private lateinit var savedFanNote: FanNoteJpaEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(id = "testMember1", memberId = "testMember1")
        savedFanNote = fanNoteJpaRepository.save(createFanNoteJpaEntity())
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("덕질노트 저장을 생성하고 ID가 생성된다")
        fun `덕질노트 저장 - 정상 케이스`() {
            val saveFanNote = SaveFanNoteEntity.create(
                memberId = savedMember.id,
                fanNoteId = savedFanNote.id!!
            )

            val saved = saveFanNotePort.save(saveFanNote)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.fanNoteId).isEqualTo(savedFanNote.id)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 저장하면 예외가 발생한다")
        fun `덕질노트 저장 - 존재하지 않는 회원`() {
            val saveFanNote = SaveFanNoteEntity.create(
                memberId = "nonExistentMember",
                fanNoteId = savedFanNote.id!!
            )

            assertThatThrownBy {
                saveFanNotePort.save(saveFanNote)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 덕질노트를 저장하면 예외가 발생한다")
        fun `덕질노트 저장 - 존재하지 않는 덕질노트`() {
            val saveFanNote = SaveFanNoteEntity.create(
                memberId = savedMember.id,
                fanNoteId = 999999L
            )

            assertThatThrownBy {
                saveFanNotePort.save(saveFanNote)
            }.isInstanceOf(FanNoteException::class.java)
                .extracting("errorCode")
                .isEqualTo(FanNoteErrorCode.FAN_NOTE_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndFanNoteId 메서드")
    inner class ExistsByMemberIdAndFanNoteId {

        @Test
        @DisplayName("저장이 존재하면 true를 반환한다")
        fun `저장 존재 확인 - 존재함`() {
            val saveFanNote = SaveFanNoteEntity.create(
                memberId = savedMember.id,
                fanNoteId = savedFanNote.id!!
            )
            saveFanNotePort.save(saveFanNote)

            val exists = saveFanNotePort.existsByMemberIdAndFanNoteId(savedMember.id, savedFanNote.id!!)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("저장이 존재하지 않으면 false를 반환한다")
        fun `저장 존재 확인 - 존재하지 않음`() {
            val exists = saveFanNotePort.existsByMemberIdAndFanNoteId(savedMember.id, savedFanNote.id!!)

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드")
    inner class FindByMemberId {

        @Test
        @DisplayName("회원이 저장한 덕질노트 목록을 페이징 조회한다")
        fun `저장 목록 조회 - 정상 케이스`() {
            val fanNote2 = fanNoteJpaRepository.save(createFanNoteJpaEntity("덕질노트2"))
            saveFanNotePort.save(SaveFanNoteEntity.create(savedMember.id, savedFanNote.id!!))
            saveFanNotePort.save(SaveFanNoteEntity.create(savedMember.id, fanNote2.id!!))
            val pageable = PageRequest.of(0, 10)

            val page = saveFanNotePort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).hasSize(2)
        }

        @Test
        @DisplayName("저장한 덕질노트가 없으면 빈 페이지를 반환한다")
        fun `저장 목록 조회 - 저장 없음`() {
            val pageable = PageRequest.of(0, 10)

            val page = saveFanNotePort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).isEmpty()
        }

        @Test
        @DisplayName("페이징으로 일부만 조회한다")
        fun `저장 목록 조회 - 페이징`() {
            val fanNote2 = fanNoteJpaRepository.save(createFanNoteJpaEntity("덕질노트2"))
            val fanNote3 = fanNoteJpaRepository.save(createFanNoteJpaEntity("덕질노트3"))
            saveFanNotePort.save(SaveFanNoteEntity.create(savedMember.id, savedFanNote.id!!))
            saveFanNotePort.save(SaveFanNoteEntity.create(savedMember.id, fanNote2.id!!))
            saveFanNotePort.save(SaveFanNoteEntity.create(savedMember.id, fanNote3.id!!))
            val pageable = PageRequest.of(0, 2)

            val page = saveFanNotePort.findByMemberId(savedMember.id, pageable)

            assertThat(page.content).hasSize(2)
            assertThat(page.totalElements).isEqualTo(3)
        }
    }

    @Nested
    @DisplayName("deleteByMemberIdAndFanNoteId 메서드")
    inner class DeleteByMemberIdAndFanNoteId {

        @Test
        @DisplayName("저장을 삭제한다")
        fun `저장 삭제 - 정상 케이스`() {
            val saveFanNote = SaveFanNoteEntity.create(
                memberId = savedMember.id,
                fanNoteId = savedFanNote.id!!
            )
            saveFanNotePort.save(saveFanNote)

            saveFanNotePort.deleteByMemberIdAndFanNoteId(savedMember.id, savedFanNote.id!!)

            val exists = saveFanNotePort.existsByMemberIdAndFanNoteId(savedMember.id, savedFanNote.id!!)
            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 저장을 삭제해도 예외가 발생하지 않는다")
        fun `저장 삭제 - 존재하지 않는 저장`() {
            saveFanNotePort.deleteByMemberIdAndFanNoteId(savedMember.id, savedFanNote.id!!)
        }
    }

    private fun createFanNoteJpaEntity(title: String = "테스트 덕질노트"): FanNoteJpaEntity {
        return FanNoteJpaEntity(
            title = title,
            subtitle = "테스트 부제목",
            content = "테스트 내용",
            productionDate = LocalDate.now()
        )
    }
}
