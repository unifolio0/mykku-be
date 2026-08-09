package com.example.mykku.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.core.task.TaskExecutor

@TestConfiguration
class TestAsyncConfig {

    @Bean(name = ["applicationTaskExecutor", "taskExecutor"])
    fun applicationTaskExecutor(): TaskExecutor = SyncTaskExecutor()

    @Bean(name = ["titleAwardExecutor"])
    fun titleAwardExecutor(): TaskExecutor = SyncTaskExecutor()
}
