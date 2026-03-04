package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.board.adapter.output.persistence.entity.BoardJpaEntity
import com.example.mykku.feed.adapter.output.persistence.FeedJpaRepository
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.feed.exception.FeedErrorCode
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import com.example.mykku.scrap.adapter.output.persistence.entity.FolderJpaEntity
import com.example.mykku.scrap.application.port.output.FolderPort
import com.example.mykku.scrap.application.port.output.SaveFeedPort
import com.example.mykku.scrap.domain.entity.FolderEntity
import com.example.mykku.scrap.domain.entity.SaveFeedEntity
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

@DisplayName("SaveFeedPersistenceAdapter 통합 테스트")
class SaveFeedPersistenceAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var saveFeedPort: SaveFeedPort

    @Autowired
    private lateinit var folderPort: FolderPort

    @Autowired
    private lateinit var feedJpaRepository: FeedJpaRepository

    @Autowired
    private lateinit var folderJpaRepository: FolderJpaRepository

    private lateinit var savedMember: MemberJpaEntity
    private lateinit var savedBoard: BoardJpaEntity
    private lateinit var savedFeed: FeedJpaEntity
    private lateinit var savedFolder: FolderEntity

    @BeforeEach
    fun setUp() {
        savedMember = createAndSaveMember(memberId = "testMember1")
        savedBoard = createAndSaveBoard()
        savedFeed = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
        savedFolder = folderPort.save(FolderEntity.create(savedMember.id, "테스트 폴더", null))
    }

    @Nested
    @DisplayName("save 메서드 - 새 저장 생성")
    inner class SaveNew {

        @Test
        @DisplayName("피드 저장을 생성하고 ID가 생성된다")
        fun `피드 저장 - 정상 케이스`() {
            val saveFeed = SaveFeedEntity.create(
                memberId = savedMember.id,
                feedId = savedFeed.id!!,
                folderId = savedFolder.id!!.value
            )

            val saved = saveFeedPort.save(saveFeed)

            assertThat(saved.id).isNotNull
            assertThat(saved.memberId).isEqualTo(savedMember.id)
            assertThat(saved.feedId).isEqualTo(savedFeed.id)
            assertThat(saved.folderId).isEqualTo(savedFolder.id!!.value)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 저장하면 예외가 발생한다")
        fun `피드 저장 - 존재하지 않는 회원`() {
            val saveFeed = SaveFeedEntity.create(
                memberId = 999999L,
                feedId = savedFeed.id!!,
                folderId = savedFolder.id!!.value
            )

            assertThatThrownBy {
                saveFeedPort.save(saveFeed)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 피드를 저장하면 예외가 발생한다")
        fun `피드 저장 - 존재하지 않는 피드`() {
            val saveFeed = SaveFeedEntity.create(
                memberId = savedMember.id,
                feedId = 999999L,
                folderId = savedFolder.id!!.value
            )

            assertThatThrownBy {
                saveFeedPort.save(saveFeed)
            }.isInstanceOf(FeedException::class.java)
                .extracting("errorCode")
                .isEqualTo(FeedErrorCode.FEED_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 폴더에 저장하면 예외가 발생한다")
        fun `피드 저장 - 존재하지 않는 폴더`() {
            val saveFeed = SaveFeedEntity.create(
                memberId = savedMember.id,
                feedId = savedFeed.id!!,
                folderId = 999999L
            )

            assertThatThrownBy {
                saveFeedPort.save(saveFeed)
            }.isInstanceOf(ScrapException::class.java)
                .extracting("errorCode")
                .isEqualTo(ScrapErrorCode.FOLDER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("save 메서드 - 폴더 변경")
    inner class SaveUpdate {

        @Test
        @DisplayName("저장된 피드의 폴더를 변경한다")
        fun `폴더 변경 - 정상 케이스`() {
            val saveFeed = SaveFeedEntity.create(
                memberId = savedMember.id,
                feedId = savedFeed.id!!,
                folderId = savedFolder.id!!.value
            )
            val saved = saveFeedPort.save(saveFeed)

            val newFolder = folderPort.save(FolderEntity.create(savedMember.id, "새 폴더", null))
            val updatedSaveFeed = SaveFeedEntity.reconstitute(
                id = saved.id!!.value,
                memberId = saved.memberId,
                feedId = saved.feedId,
                folderId = newFolder.id!!.value,
                createdAt = saved.createdAt,
                updatedAt = saved.updatedAt
            )

            val updated = saveFeedPort.save(updatedSaveFeed)

            assertThat(updated.folderId).isEqualTo(newFolder.id!!.value)
        }

        @Test
        @DisplayName("존재하지 않는 저장을 수정하면 예외가 발생한다")
        fun `폴더 변경 - 존재하지 않는 저장`() {
            val saveFeed = SaveFeedEntity.reconstitute(
                id = 999999L,
                memberId = savedMember.id,
                feedId = savedFeed.id!!,
                folderId = savedFolder.id!!.value,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )

            assertThatThrownBy {
                saveFeedPort.save(saveFeed)
            }.isInstanceOf(ScrapException::class.java)
                .extracting("errorCode")
                .isEqualTo(ScrapErrorCode.SAVE_FEED_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndFeedId 메서드")
    inner class ExistsByMemberIdAndFeedId {

        @Test
        @DisplayName("저장이 존재하면 true를 반환한다")
        fun `저장 존재 확인 - 존재함`() {
            val saveFeed = SaveFeedEntity.create(
                memberId = savedMember.id,
                feedId = savedFeed.id!!,
                folderId = savedFolder.id!!.value
            )
            saveFeedPort.save(saveFeed)

            val exists = saveFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("저장이 존재하지 않으면 false를 반환한다")
        fun `저장 존재 확인 - 존재하지 않음`() {
            val exists = saveFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("findByMemberIdAndFeedIdIn 메서드")
    inner class FindByMemberIdAndFeedIdIn {

        @Test
        @DisplayName("회원이 저장한 피드 목록을 조회한다")
        fun `저장된 피드 조회 - 정상 케이스`() {
            val feed2 = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
            saveFeedPort.save(SaveFeedEntity.create(savedMember.id, savedFeed.id!!, savedFolder.id!!.value))
            saveFeedPort.save(SaveFeedEntity.create(savedMember.id, feed2.id!!, savedFolder.id!!.value))

            val saves = saveFeedPort.findByMemberIdAndFeedIdIn(
                savedMember.id,
                listOf(savedFeed.id!!, feed2.id!!)
            )

            assertThat(saves).hasSize(2)
        }

        @Test
        @DisplayName("빈 피드 ID 목록으로 조회하면 빈 리스트를 반환한다")
        fun `저장된 피드 조회 - 빈 목록`() {
            val saves = saveFeedPort.findByMemberIdAndFeedIdIn(savedMember.id, emptyList())

            assertThat(saves).isEmpty()
        }
    }

    @Nested
    @DisplayName("findByMemberIdAndFolderId 메서드")
    inner class FindByMemberIdAndFolderId {

        @Test
        @DisplayName("폴더별 저장된 피드를 페이징 조회한다")
        fun `폴더별 저장 조회 - 정상 케이스`() {
            val feed2 = feedJpaRepository.save(createFeedJpaEntity(savedBoard, savedMember))
            saveFeedPort.save(SaveFeedEntity.create(savedMember.id, savedFeed.id!!, savedFolder.id!!.value))
            saveFeedPort.save(SaveFeedEntity.create(savedMember.id, feed2.id!!, savedFolder.id!!.value))
            val pageable = PageRequest.of(0, 10)

            val page = saveFeedPort.findByMemberIdAndFolderId(savedMember.id, savedFolder.id!!.value, pageable)

            assertThat(page.content).hasSize(2)
        }

        @Test
        @DisplayName("folderId가 null이면 folder_id가 null인 저장만 조회한다")
        fun `폴더별 저장 조회 - folderId null 조건`() {
            saveFeedPort.save(SaveFeedEntity.create(savedMember.id, savedFeed.id!!, savedFolder.id!!.value))
            val pageable = PageRequest.of(0, 10)

            val page = saveFeedPort.findByMemberIdAndFolderId(savedMember.id, null, pageable)

            assertThat(page.content).isEmpty()
        }
    }

    @Nested
    @DisplayName("findByMemberIdAndFeedId 메서드")
    inner class FindByMemberIdAndFeedId {

        @Test
        @DisplayName("회원과 피드 ID로 저장을 조회한다")
        fun `저장 조회 - 정상 케이스`() {
            saveFeedPort.save(SaveFeedEntity.create(savedMember.id, savedFeed.id!!, savedFolder.id!!.value))

            val found = saveFeedPort.findByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)

            assertThat(found).isNotNull
            assertThat(found!!.feedId).isEqualTo(savedFeed.id)
        }

        @Test
        @DisplayName("저장이 없으면 null을 반환한다")
        fun `저장 조회 - 저장 없음`() {
            val found = saveFeedPort.findByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)

            assertThat(found).isNull()
        }
    }

    @Nested
    @DisplayName("deleteByMemberIdAndFeedId 메서드")
    inner class DeleteByMemberIdAndFeedId {

        @Test
        @DisplayName("저장을 삭제한다")
        fun `저장 삭제 - 정상 케이스`() {
            saveFeedPort.save(SaveFeedEntity.create(savedMember.id, savedFeed.id!!, savedFolder.id!!.value))

            saveFeedPort.deleteByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)

            val exists = saveFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)
            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 저장을 삭제해도 예외가 발생하지 않는다")
        fun `저장 삭제 - 존재하지 않는 저장`() {
            saveFeedPort.deleteByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)
        }
    }

    @Nested
    @DisplayName("deleteAllByFeedId 메서드")
    inner class DeleteAllByFeedId {

        @Test
        @DisplayName("피드의 모든 저장을 삭제한다")
        fun `피드 저장 전체 삭제 - 정상 케이스`() {
            val member2 = createAndSaveMember(memberId = "testMember2", email = "test2@example.com", socialId = "22222")
            val folder2 = folderPort.save(FolderEntity.create(member2.id, "폴더2", null))
            saveFeedPort.save(SaveFeedEntity.create(savedMember.id, savedFeed.id!!, savedFolder.id!!.value))
            saveFeedPort.save(SaveFeedEntity.create(member2.id, savedFeed.id!!, folder2.id!!.value))

            saveFeedPort.deleteAllByFeedId(savedFeed.id!!)

            assertThat(saveFeedPort.existsByMemberIdAndFeedId(savedMember.id, savedFeed.id!!)).isFalse()
            assertThat(saveFeedPort.existsByMemberIdAndFeedId(member2.id, savedFeed.id!!)).isFalse()
        }
    }

    private fun createFeedJpaEntity(board: BoardJpaEntity, member: MemberJpaEntity): FeedJpaEntity {
        return FeedJpaEntity(
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
    }
}
