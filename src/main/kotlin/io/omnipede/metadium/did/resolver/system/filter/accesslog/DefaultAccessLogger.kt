package io.omnipede.metadium.did.resolver.system.filter.accesslog

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * AccessLogFilter 생성 시 직접 AccessLogger 인터페이스에 대한 구현체를 작성하여 filter 생성자에
 * 넣어주지 않을 경우 자동 설정 되는 구현체.
 */
class DefaultAccessLogger  {

    private val objectMapper = ObjectMapper()

    fun log(accessLog: AccessLog?) {
        val message = objectMapper.writeValueAsString(accessLog)
        logger.info(message)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DefaultAccessLogger::class.java)
    }
}
