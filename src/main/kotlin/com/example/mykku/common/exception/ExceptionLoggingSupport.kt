package com.example.mykku.common.exception

import org.slf4j.Logger
import org.slf4j.MDC

object ExceptionLoggingSupport {
    fun logException(logger: Logger, exception: Exception) {
        val requestId = MDC.get("req-id") ?: "unknown"
        logger.error("[$requestId] ${exception::class.simpleName}: ${exception.message}", exception)
    }
}
