package com.example.mykku.scrap.adapter.output.persistence

import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.scrap.adapter.output.persistence.entity.FolderJpaEntity
import com.example.mykku.scrap.application.port.output.FolderPort
import com.example.mykku.scrap.domain.entity.FolderEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class FolderPersistenceAdapter(
    private val folderJpaRepository: FolderJpaRepository,
    private val memberRepository: MemberRepository
) : FolderPort {

    override fun save(folder: FolderEntity): FolderEntity {
        val member = memberRepository.findByIdOrNull(folder.memberId)
            ?: throw IllegalArgumentException("Member not found: ${folder.memberId}")

        val jpaEntity = if (folder.id != null) {
            val existing = folderJpaRepository.findByIdOrNull(folder.id.value)
                ?: throw IllegalArgumentException("Folder not found: ${folder.id.value}")
            existing.updateInfo(folder.name, folder.description)
            existing
        } else {
            FolderJpaEntity.fromDomain(folder, member)
        }

        return folderJpaRepository.save(jpaEntity).toDomain()
    }

    override fun findByMemberIdAndId(memberId: String, id: Long): FolderEntity? {
        return folderJpaRepository.findByMemberIdAndId(memberId, id)?.toDomain()
    }

    override fun findByMemberId(memberId: String): List<FolderEntity> {
        return folderJpaRepository.findByMemberId(memberId).map { it.toDomain() }
    }

    override fun existsByMemberIdAndName(memberId: String, name: String): Boolean {
        return folderJpaRepository.existsByMemberIdAndName(memberId, name)
    }

    override fun delete(folder: FolderEntity) {
        val jpaEntity = folderJpaRepository.findByIdOrNull(folder.id!!.value)
            ?: throw IllegalArgumentException("Folder not found: ${folder.id.value}")
        folderJpaRepository.delete(jpaEntity)
    }
}
