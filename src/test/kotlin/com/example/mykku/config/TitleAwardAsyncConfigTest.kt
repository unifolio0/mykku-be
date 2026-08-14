package com.example.mykku.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.core.task.TaskExecutor
import java.util.concurrent.Executor

@DisplayName("TitleAwardAsyncConfig 배선 테스트")
class TitleAwardAsyncConfigTest {

    companion object {
        private const val TITLE_AWARD_EXECUTOR = "titleAwardExecutor"
    }

    private val contextRunner = ApplicationContextRunner()
        .withUserConfiguration(TitleAwardAsyncConfig::class.java)
        .withConfiguration(AutoConfigurations.of(TaskExecutionAutoConfiguration::class.java))

    @Test
    @DisplayName("칭호 전용 executor를 등록해도 Spring Boot 기본 executor 자동 설정이 유지된다")
    fun keepsBootDefaultTaskExecutorAutoConfiguration() {
        contextRunner.run { context ->
            assertThat(context).hasBean("applicationTaskExecutor")
            assertThat(context).hasBean(TITLE_AWARD_EXECUTOR)
        }
    }

    @Test
    @DisplayName("한정자 없는 @Async가 해석하는 기본 executor는 칭호 전용 executor가 아니다")
    fun defaultAsyncExecutorIsNotTitleAwardExecutor() {
        contextRunner.run { context ->
            assertThat(defaultAsyncExecutor(context)).isNotSameAs(titleAwardExecutor(context))
        }
    }

    @Test
    @DisplayName("@Async 한정자 해석 경로가 칭호 전용 executor를 찾는다")
    fun asyncQualifierResolvesTitleAwardExecutor() {
        contextRunner.run { context ->
            val resolved = BeanFactoryAnnotationUtils.qualifiedBeanOfType(
                context.beanFactory,
                Executor::class.java,
                TITLE_AWARD_EXECUTOR
            )

            assertThat(resolved).isSameAs(titleAwardExecutor(context))
        }
    }

    private fun defaultAsyncExecutor(context: AssertableApplicationContext): TaskExecutor =
        context.getBean(TaskExecutor::class.java)

    private fun titleAwardExecutor(context: AssertableApplicationContext): TaskExecutor =
        context.getBean(TITLE_AWARD_EXECUTOR, TaskExecutor::class.java)
}
