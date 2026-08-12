package com.example.mykku.config

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class TitleAwardAsyncConfig {

    companion object {
        private const val QUEUE_CAPACITY = 500
        private const val THREAD_NAME_PREFIX = "title-award-"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean(name = ["titleAwardExecutor"], defaultCandidate = false)
    @ConditionalOnMissingBean(name = ["titleAwardExecutor"])
    fun titleAwardExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 1
        executor.maxPoolSize = 1
        executor.queueCapacity = QUEUE_CAPACITY
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX)
        executor.setRejectedExecutionHandler { _, _ -> logRejection() }
        executor.initialize()
        return executor
    }

    private fun logRejection() {
        log.error("칭호 부여 작업 큐가 포화되어 작업을 폐기했습니다: queueCapacity={}", QUEUE_CAPACITY)
    }
}
