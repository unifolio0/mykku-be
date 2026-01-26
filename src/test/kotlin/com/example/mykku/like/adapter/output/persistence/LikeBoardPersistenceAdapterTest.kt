package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.board.exception.BoardErrorCode
import com.example.mykku.board.exception.BoardException
import com.example.mykku.like.application.port.output.LikeBoardPort
import com.example.mykku.like.domain.entity.LikeBoardEntity
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
import org.springframework.data.domain.PageRequest

@DisplayName("LikeBoardPersistenceAdapter 통합 테스트")
class LikeBoardPersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var likeBoardPort: LikeBoardPort

    private lateinit var savedMember: MemberJpaEntity
    private lateinit var savedBoard: BoardJpaEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(id = "testMember1", memberId = "testMember1")
        savedBoard = createAndSaveBoard()
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("게시판 좋아요를 저장하고 ID가 생성된다")
        fun `게시판 좋아요 저장 - 정상 케이스`() {
            val likeBoard = LikeBoardEntity.create(
                memberId = savedMember.id,
                boardId = savedBoard.id!!
            )

            val saved = likeBoardPort.save(likeBoard)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.boardId).isEqualTo(savedBoard.id)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 좋아요하면 예외가 발생한다")
        fun `게시판 좋아요 저장 - 존재하지 않는 회원`() {
            val likeBoard = LikeBoardEntity.create(
                memberId = "nonExistentMember",
                boardId = savedBoard.id!!
            )

            assertThatThrownBy {
                likeBoardPort.save(likeBoard)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 게시판에 좋아요하면 예외가 발생한다")
        fun `게시판 좋아요 저장 - 존재하지 않는 게시판`() {
            val likeBoard = LikeBoardEntity.create(
                memberId = savedMember.id,
                boardId = 999999L
            )

            assertThatThrownBy {
                likeBoardPort.save(likeBoard)
            }.isInstanceOf(BoardException::class.java)
                .extracting("errorCode")
                .isEqualTo(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndBoardId 메서드")
    inner class ExistsByMemberIdAndBoardId {

        @Test
        @DisplayName("좋아요가 존재하면 true를 반환한다")
        fun `좋아요 존재 확인 - 존재함`() {
            val likeBoard = LikeBoardEntity.create(
                memberId = savedMember.id,
                boardId = savedBoard.id!!
            )
            likeBoardPort.save(likeBoard)

            val exists = likeBoardPort.existsByMemberIdAndBoardId(savedMember.id, savedBoard.id!!)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("좋아요가 존재하지 않으면 false를 반환한다")
        fun `좋아요 존재 확인 - 존재하지 않음`() {
            val exists = likeBoardPort.existsByMemberIdAndBoardId(savedMember.id, savedBoard.id!!)

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("deleteByMemberIdAndBoardId 메서드")
    inner class DeleteByMemberIdAndBoardId {

        @Test
        @DisplayName("좋아요를 삭제한다")
        fun `좋아요 삭제 - 정상 케이스`() {
            val likeBoard = LikeBoardEntity.create(
                memberId = savedMember.id,
                boardId = savedBoard.id!!
            )
            likeBoardPort.save(likeBoard)

            likeBoardPort.deleteByMemberIdAndBoardId(savedMember.id, savedBoard.id!!)

            val exists = likeBoardPort.existsByMemberIdAndBoardId(savedMember.id, savedBoard.id!!)
            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 좋아요를 삭제해도 예외가 발생하지 않는다")
        fun `좋아요 삭제 - 존재하지 않는 좋아요`() {
            likeBoardPort.deleteByMemberIdAndBoardId(savedMember.id, savedBoard.id!!)
        }
    }

    @Nested
    @DisplayName("findAllByMemberId 메서드")
    inner class FindAllByMemberId {

        @Test
        @DisplayName("회원이 좋아요한 게시판 목록을 조회한다")
        fun `좋아요한 게시판 조회 - 정상 케이스`() {
            val board2 = createAndSaveBoard(title = "테스트 게시판 2")
            likeBoardPort.save(LikeBoardEntity.create(savedMember.id, savedBoard.id!!))
            likeBoardPort.save(LikeBoardEntity.create(savedMember.id, board2.id!!))

            val likes = likeBoardPort.findAllByMemberId(savedMember.id)

            assertThat(likes).hasSize(2)
        }

        @Test
        @DisplayName("좋아요한 게시판이 없으면 빈 리스트를 반환한다")
        fun `좋아요한 게시판 조회 - 빈 목록`() {
            val likes = likeBoardPort.findAllByMemberId(savedMember.id)

            assertThat(likes).isEmpty()
        }
    }

    @Nested
    @DisplayName("findAllByMemberIdWithBoardInfo 메서드")
    inner class FindAllByMemberIdWithBoardInfo {

        @Test
        @DisplayName("회원이 좋아요한 게시판 정보를 페이징 조회한다")
        fun `좋아요한 게시판 정보 조회 - 정상 케이스`() {
            val board2 = createAndSaveBoard(title = "테스트 게시판 2")
            likeBoardPort.save(LikeBoardEntity.create(savedMember.id, savedBoard.id!!))
            likeBoardPort.save(LikeBoardEntity.create(savedMember.id, board2.id!!))
            val pageable = PageRequest.of(0, 10)

            val page = likeBoardPort.findAllByMemberIdWithBoardInfo(savedMember.id, pageable)

            assertThat(page.content).hasSize(2)
            assertThat(page.content[0].title).isNotBlank()
        }

        @Test
        @DisplayName("페이징으로 일부만 조회한다")
        fun `좋아요한 게시판 정보 조회 - 페이징`() {
            val board2 = createAndSaveBoard(title = "테스트 게시판 2")
            val board3 = createAndSaveBoard(title = "테스트 게시판 3")
            likeBoardPort.save(LikeBoardEntity.create(savedMember.id, savedBoard.id!!))
            likeBoardPort.save(LikeBoardEntity.create(savedMember.id, board2.id!!))
            likeBoardPort.save(LikeBoardEntity.create(savedMember.id, board3.id!!))
            val pageable = PageRequest.of(0, 2)

            val page = likeBoardPort.findAllByMemberIdWithBoardInfo(savedMember.id, pageable)

            assertThat(page.content).hasSize(2)
            assertThat(page.totalElements).isEqualTo(3)
            assertThat(page.totalPages).isEqualTo(2)
        }
    }
}
