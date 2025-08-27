package com.example.mykku.util

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate

class DatabaseCleaner : BeforeEachCallback {

    override fun beforeEach(extensionContext: ExtensionContext) {
        val context = SpringExtension.getApplicationContext(extensionContext)
        cleanup(context)
    }

    private fun cleanup(context: ApplicationContext) {
        val em = context.getBean(EntityManager::class.java)
        val transactionTemplate = context.getBean(TransactionTemplate::class.java)

        transactionTemplate.execute {
            em.clear()
            truncateTables(em)
            null
        }
    }

    private fun truncateTables(em: EntityManager) {
        // H2 데이터베이스의 경우 외래 키 검사 비활성화
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()
        
        val tableNames = findTableNames(em)
        tableNames.forEach { tableName ->
            em.createNativeQuery("TRUNCATE TABLE $tableName").executeUpdate()
        }
        
        // H2 데이터베이스의 경우 외래 키 검사 다시 활성화
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()
    }

    @Suppress("UNCHECKED_CAST")
    private fun findTableNames(em: EntityManager): List<String> {
        val tableNameSelectQuery = """
            SELECT TABLE_NAME
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_SCHEMA = 'PUBLIC'
            AND TABLE_TYPE = 'TABLE'
            AND TABLE_NAME NOT LIKE 'FLYWAY_%'
        """.trimIndent()

        return em.createNativeQuery(tableNameSelectQuery)
            .resultList as List<String>
    }
}