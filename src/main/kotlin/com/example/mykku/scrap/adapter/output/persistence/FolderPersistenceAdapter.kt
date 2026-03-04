package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.member.exception.MemberException
import com.example.mykku.scrap.adapter.output.persistence.entity.FolderJpaEntity
import com.example.mykku.scrap.application.port.output.FolderPort
import com.example.mykku.scrap.domain.entity.FolderEntity
import com.example.mykku.scrap.exception.ScrapException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class FolderPersistenceAdapter(
    private val folderJpaRepository: FolderJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) : FolderPort {

    override fun save(folder: FolderEntity): FolderEntity {
        val member = memberJpaRepository.findByIdOrNull(folder.memberId)
            ?: throw MemberException.memberNotFound()

        val jpaEntity = if (folder.id != null) {
            val existing = folderJpaRepository.findByIdOrNull(folder.id.value)
                ?: throw ScrapException.folderNotFound()
            existing.updateInfo(folder.name, folder.description)
            existing
        } else {
            FolderJpaEntity.fromDomain(folder, member)
        }

        return folderJpaRepository.save(jpaEntity).toDomain()
    }

    override fun findByMemberIdAndId(memberId: Long, id: Long): FolderEntity? {
        return folderJpaRepository.findByMemberIdAndId(memberId, id)?.toDomain()
    }

    override fun findByMemberId(memberId: Long): List<FolderEntity> {
        return folderJpaRepository.findByMemberId(memberId).map { it.toDomain() }
    }

    override fun existsByMemberIdAndName(memberId: Long, name: String): Boolean {
        return folderJpaRepository.existsByMemberIdAndName(memberId, name)
    }

    override fun delete(folder: FolderEntity) {
        val jpaEntity = folderJpaRepository.findByIdOrNull(folder.id!!.value)
            ?: throw ScrapException.folderNotFound()
        folderJpaRepository.delete(jpaEntity)
    }
}
