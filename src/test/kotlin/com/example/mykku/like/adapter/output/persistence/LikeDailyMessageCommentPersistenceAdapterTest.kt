package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.dailymessage.adapter.output.persistence.entity.DailyMessageCommentJpaEntity
import com.example.mykku.dailymessage.adapter.output.persistence.entity.DailyMessageJpaEntity
import com.example.mykku.dailymessage.adapter.output.persistence.repository.DailyMessageCommentJpaRepository
import com.example.mykku.dailymessage.adapter.output.persistence.repository.DailyMessageJpaRepository
import com.example.mykku.dailymessage.exception.DailyMessageErrorCode
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.like.application.port.output.LikeDailyMessageCommentPort
import com.example.mykku.like.domain.entity.LikeDailyMessageCommentEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@DisplayName("LikeDailyMessageCommentPersistenceAdapter 통합 테스트")
class LikeDailyMessageCommentPersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var likeDailyMessageCommentPort: LikeDailyMessageCommentPort

    @Autowired
    private lateinit var dailyMessageJpaRepository: DailyMessageJpaRepository

    @Autowired
    private lateinit var dailyMessageCommentJpaRepository: DailyMessageCommentJpaRepository

    private lateinit var savedMember: MemberJpaEntity
    private lateinit var savedDailyMessage: DailyMessageJpaEntity
    private lateinit var savedComment: DailyMessageCommentJpaEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(memberId = "testMember1")
        savedDailyMessage = dailyMessageJpaRepository.save(createDailyMessageJpaEntity())
        savedComment = dailyMessageCommentJpaRepository.save(createDailyMessageCommentJpaEntity(savedDailyMessage, savedMember))
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("하루덕담 댓글 좋아요를 저장하고 ID가 생성된다")
        fun `하루덕담 댓글 좋아요 저장 - 정상 케이스`() {
            val likeComment = LikeDailyMessageCommentEntity.create(
                memberId = savedMember.id,
                dailyMessageCommentId = savedComment.id!!
            )

            val saved = likeDailyMessageCommentPort.save(likeComment)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.dailyMessageCommentId).isEqualTo(savedComment.id)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 좋아요하면 예외가 발생한다")
        fun `하루덕담 댓글 좋아요 저장 - 존재하지 않는 회원`() {
            val likeComment = LikeDailyMessageCommentEntity.create(
                memberId = 999999L,
                dailyMessageCommentId = savedComment.id!!
            )

            assertThatThrownBy {
                likeDailyMessageCommentPort.save(likeComment)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 댓글에 좋아요하면 예외가 발생한다")
        fun `하루덕담 댓글 좋아요 저장 - 존재하지 않는 댓글`() {
            val likeComment = LikeDailyMessageCommentEntity.create(
                memberId = savedMember.id,
                dailyMessageCommentId = 999999L
            )

            assertThatThrownBy {
                likeDailyMessageCommentPort.save(likeComment)
            }.isInstanceOf(DailyMessageException::class.java)
                .extracting("errorCode")
                .isEqualTo(DailyMessageErrorCode.DAILY_MESSAGE_COMMENT_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndDailyMessageCommentId 메서드")
    inner class ExistsByMemberIdAndDailyMessageCommentId {

        @Test
        @DisplayName("좋아요가 존재하면 true를 반환한다")
        fun `좋아요 존재 확인 - 존재함`() {
            val likeComment = LikeDailyMessageCommentEntity.create(
                memberId = savedMember.id,
                dailyMessageCommentId = savedComment.id!!
            )
            likeDailyMessageCommentPort.save(likeComment)

            val exists = likeDailyMessageCommentPort.existsByMemberIdAndDailyMessageCommentId(savedMember.id, savedComment.id!!)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("좋아요가 존재하지 않으면 false를 반환한다")
        fun `좋아요 존재 확인 - 존재하지 않음`() {
            val exists = likeDailyMessageCommentPort.existsByMemberIdAndDailyMessageCommentId(savedMember.id, savedComment.id!!)

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("deleteByMemberIdAndDailyMessageCommentId 메서드")
    inner class DeleteByMemberIdAndDailyMessageCommentId {

        @Test
        @DisplayName("좋아요를 삭제한다")
        fun `좋아요 삭제 - 정상 케이스`() {
            val likeComment = LikeDailyMessageCommentEntity.create(
                memberId = savedMember.id,
                dailyMessageCommentId = savedComment.id!!
            )
            likeDailyMessageCommentPort.save(likeComment)

            likeDailyMessageCommentPort.deleteByMemberIdAndDailyMessageCommentId(savedMember.id, savedComment.id!!)

            val exists = likeDailyMessageCommentPort.existsByMemberIdAndDailyMessageCommentId(savedMember.id, savedComment.id!!)
            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 좋아요를 삭제해도 예외가 발생하지 않는다")
        fun `좋아요 삭제 - 존재하지 않는 좋아요`() {
            likeDailyMessageCommentPort.deleteByMemberIdAndDailyMessageCommentId(savedMember.id, savedComment.id!!)
        }
    }

    private fun createDailyMessageJpaEntity(): DailyMessageJpaEntity {
        return DailyMessageJpaEntity(
            title = "테스트 하루덕담",
            content = "테스트 내용",
            date = LocalDate.now()
        )
    }

    private fun createDailyMessageCommentJpaEntity(
        dailyMessage: DailyMessageJpaEntity,
        member: MemberJpaEntity
    ): DailyMessageCommentJpaEntity {
        return DailyMessageCommentJpaEntity(
            content = "테스트 댓글",
            dailyMessage = dailyMessage,
            member = member
        )
    }
}
