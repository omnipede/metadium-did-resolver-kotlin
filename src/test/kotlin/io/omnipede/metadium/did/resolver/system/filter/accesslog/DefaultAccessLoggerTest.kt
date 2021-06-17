package io.omnipede.metadium.did.resolver.system.filter.accesslog

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class DefaultAccessLoggerTest {

    @Test
    @DisplayName("Slf4j 로깅 테스트")
    fun slf4j_logging_test() {

        // Given
        val logger: Logger = LoggerFactory.getLogger(DefaultAccessLogger::class.java) as Logger
        val defaultAccessLogger = DefaultAccessLogger()

        val listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()

        logger.addAppender(listAppender)

        // When
        defaultAccessLogger.log(null)

        // Then
        val logList = listAppender.list
        assertThat(logList.size).isEqualTo(1)
        assertThat(logList[0].level).isEqualTo(Level.INFO)
    }
}
