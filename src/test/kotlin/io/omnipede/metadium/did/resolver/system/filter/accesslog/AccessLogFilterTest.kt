package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.apache.commons.io.IOUtils
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.invocation.InvocationOnMock
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class AccessLogFilterTest {

    private var accessLogCaptor: ArgumentCaptor<AccessLog>? = ArgumentCaptor.forClass(AccessLog::class.java)
    private var filterChain: FilterChain? = null
    private var defaultAccessLogger: DefaultAccessLogger? = null

    @BeforeEach
    fun setup() {
        filterChain = mock(FilterChain::class.java)
        defaultAccessLogger = mock(DefaultAccessLogger::class.java)
        doNothing()
            .`when`(defaultAccessLogger)!!
            .log(any(AccessLog::class.java))
    }

    companion object {
        @JvmStatic
        fun test_content_logging_argumentsProvider(): Stream<Arguments>? {

            return Stream.of(
                // (Request body, Response body)
                Arguments.of("Hello world", "Hello world response"),
                Arguments.of("!@#$%     !@#$    ", "Hello world response"),
                Arguments.of("Hello world", "!@#\$% \\\\\\\\ \\\\1234%%%    !@#\$    "),
                Arguments.of("Hello world", "!@#\$%     !@#\$    "),

                // Request body, response body 가 아주 길 때
                Arguments.of(
                    "01234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234",
                    "01234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234"
                ),
                Arguments.of(null, null)
            )
        }

        @JvmStatic
        fun test_ip_logging_argumentsProvider(): Stream<Arguments>? {
            val ip = "My-Ip-address"
            val blankIp = ""
            val unknownIp = "unknown"

            return Stream.of(
                // (headerName, headerValue)
                Arguments.of("X-Forwarded-For", ip),
                Arguments.of("Proxy-Client-IP", ip),
                Arguments.of("WL-Proxy-Client-IP", ip),
                Arguments.of("HTTP_CLIENT_IP", ip),
                Arguments.of("HTTP_X_FORWARDED_FOR", ip),
                Arguments.of(null, ip),
                Arguments.of("HTTP_X_FORWARDED_FOR", blankIp),
                Arguments.of("HTTP_X_FORWARDED_FOR", unknownIp)
            )
        }
    }

    @ParameterizedTest(name = "Request/response body 로깅 테스트: {index}")
    @MethodSource("test_content_logging_argumentsProvider")
    fun test_content_logging(requestBody: String?, responseBody: String?) {

        // Given
        val httpServletRequest = givenMockHttpServletRequest()
        httpServletRequest.setContent(requestBody?.toByteArray())

        val httpServletResponse = givenMockHttpServletResponse()

        if (responseBody != null) {
            // filterChain.doFilter 메소드에서 두 번째 argument 인 CachingResponseWrapper 를 이용하도록 mocking 함
            doAnswer { invocationOnMock: InvocationOnMock ->

                // 테스트용으로 getReader() 메소드로 request body 를 읽어본다
                val requestWrapper =
                    invocationOnMock.arguments[0] as HttpServletRequest
                val inputStream = requestWrapper.inputStream

                // 테스트용으로 getInputStream() 메소드로 request body 를 읽어본다
                val content = IOUtils.toString(inputStream)
                if (requestBody != null)
                    assertThat(content).isEqualTo(requestBody)

                // 테스트용으로 output stream 에 response body 를 쓴다
                val responseWrapper =
                    invocationOnMock.arguments[1] as HttpServletResponse
                val outputStream: OutputStream = responseWrapper.outputStream
                outputStream.write(responseBody.toByteArray())
            }
                .`when`(filterChain)!!
                .doFilter(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java))
        }

        // 최대 contents length
        val maxContentsLength = 1024
        val accessLogFilterConfigurer = AccessLogFilterConfigurer(
           maxContentLength = maxContentsLength, enableContentLogging = true
        )
        val accessLogFilter = AccessLogFilter(accessLogFilterConfigurer, defaultAccessLogger!!)

        // When
        val start = Date()
        accessLogFilter.doFilter(httpServletRequest, httpServletResponse, filterChain!!)
        val duration = Date().time - start.time

        // Then
        verify(defaultAccessLogger, times(1))!!
            .log(accessLogCaptor!!.capture())

        val accessLog = accessLogCaptor!!.value
        assertThat(accessLog).isNotNull

        // Request body 확인
        when {
            requestBody == null -> assertThat(accessLog.requestBody).isEqualTo("")
            requestBody.length > maxContentsLength -> assertThat(accessLog.requestBody).isEqualTo("TOO LONG CONTENTS")
            else -> assertThat(accessLog.requestBody).isEqualTo(requestBody)
        }

        // Response body 확인
        when {
            responseBody == null -> assertThat(accessLog.responseBody).isEqualTo("")
            responseBody.length > maxContentsLength -> assertThat(accessLog.responseBody).isEqualTo("TOO LONG CONTENTS")
            else -> assertThat(accessLog.responseBody).isEqualTo(responseBody)
        }

        assertThat(accessLog.method).isEqualTo(httpServletRequest.method)
        assertThat(accessLog.uri).isEqualTo(httpServletRequest.requestURI)
        assertThat(accessLog.query).isEqualTo(httpServletRequest.queryString)
        assertThat(accessLog.requestAt).isBefore(Date())
        assertThat(accessLog.responseAt).isBefore(Date())
        assertThat(accessLog.status).isEqualTo(httpServletResponse.status)
        assertThat(accessLog.userAgent).isEqualTo("Unknown")
        assertThat(accessLog.elapsed).isLessThan(duration)

        // Request header 확인
        val requestHeaders = Collections.list(httpServletRequest.headerNames).associateWith { headerName ->
            httpServletRequest.getHeader(headerName)
        }
        assertThat(accessLog.requestHeaders.filterKeys { it != "DeviceClass" }).isEqualTo(requestHeaders)

        // Response header 확인
        val responseHeaders = httpServletResponse.headerNames.associateWith { headerName ->
            httpServletResponse.getHeader(headerName)
        }
        assertThat(accessLog.responseHeaders).isEqualTo(responseHeaders)
    }

    @Test
    @DisplayName("Whitelist 된 URI 에 대한 access log 는 로깅하면 안된다")
    fun test_whiteListing() {

        // Given
        val requestBody = "Hello world"
        val requestUri = "/api/v1/temp"
        val httpServletRequest = givenMockHttpServletRequest()
        httpServletRequest.requestURI = requestUri
        httpServletRequest.setContent(requestBody.toByteArray())

        val httpServletResponse = givenMockHttpServletResponse()

        // AccessLog configurer 설정
        val accessLogFilterConfigurer = AccessLogFilterConfigurer(
            whiteList = listOf("/api/v1"),
            maxContentLength = 1024,
            enableContentLogging = true
        )
        val accessLogFilter = AccessLogFilter(accessLogFilterConfigurer, defaultAccessLogger!!)

        // When
        accessLogFilter.doFilter(httpServletRequest, httpServletResponse, filterChain!!)

        // Then
        verify(defaultAccessLogger, times(0))!!
            .log(any(AccessLog::class.java))
    }

    @DisplayName("enableContentLogging = false 일 경우 content 를 로깅하면 안된다")
    @Test
    fun test_disable_content_logging() {

        // Given
        val requestBody = "Hello world"
        val responseBody = "Hello response"

        val httpServletRequest = givenMockHttpServletRequest()
        httpServletRequest.setContent(requestBody.toByteArray())

        val httpServletResponse = givenMockHttpServletResponse()

        // filterChain.doFilter 메소드에서 두 번째 argument 인 CachingResponseWrapper 를 이용하도록 mocking 함
        doAnswer { invocationOnMock: InvocationOnMock ->

            // 테스트용으로 output stream 에 response body 를 쓴다
            val responseWrapper =
                invocationOnMock.arguments[1] as HttpServletResponse
            val outputStream: OutputStream = responseWrapper.outputStream
            outputStream.write(responseBody.toByteArray())
        }
            .`when`(filterChain)!!
            .doFilter(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java))

        // 최대 contents length
        val accessLogFilterConfigurer = AccessLogFilterConfigurer(
            whiteList = emptyList(),
            maxContentLength = 1024,
            enableContentLogging = false
        )
        val accessLogFilter = AccessLogFilter(accessLogFilterConfigurer, defaultAccessLogger!!)

        // When
        val start = Date()
        accessLogFilter.doFilter(httpServletRequest, httpServletResponse, filterChain!!)
        val duration = Date().time - start.time

        // Then
        verify(defaultAccessLogger, times(1))!!
            .log(accessLogCaptor!!.capture())

        val accessLog = accessLogCaptor!!.value
        assertThat(accessLog).isNotNull

        // Request body 확인
        assertThat(accessLog.requestBody).isNull()

        // Response body 확인
        assertThat(accessLog.responseBody).isNull()

        assertThat(accessLog.method).isEqualTo(httpServletRequest.method)
        assertThat(accessLog.uri).isEqualTo(httpServletRequest.requestURI)
        assertThat(accessLog.query).isEqualTo(httpServletRequest.queryString)
        assertThat(accessLog.requestAt).isBefore(Date())
        assertThat(accessLog.responseAt).isBefore(Date())
        assertThat(accessLog.status).isEqualTo(httpServletResponse.status)
        assertThat(accessLog.userAgent).isEqualTo("Unknown")
        assertThat(accessLog.elapsed).isLessThan(duration)

        // Request header 확인
        val requestHeaders = Collections.list(httpServletRequest.headerNames).associateWith { headerName ->
            httpServletRequest.getHeader(headerName)
        }
        assertThat(accessLog.requestHeaders.filterKeys { it != "DeviceClass" }).isEqualTo(requestHeaders)

        // Response header 확인
        val responseHeaders = httpServletResponse.headerNames.associateWith { headerName ->
            httpServletResponse.getHeader(headerName)
        }
        assertThat(accessLog.responseHeaders).isEqualTo(responseHeaders)
    }

    @ParameterizedTest(name = "특정 request header 에 ip 가 세팅되었을 때 적절히 로깅하는지 확인한다: {0}, {1}")
    @MethodSource("test_ip_logging_argumentsProvider")
    fun test_ip_logging(header: String?, ip: String) {

        // Given
        val httpServletRequest = givenMockHttpServletRequest()
        val httpServletResponse = givenMockHttpServletResponse()

        if(header == null)
            httpServletRequest.remoteAddr = ip
        else
            // Set ip addr to specific header
            httpServletRequest.addHeader(header, ip)

        // 최대 contents length
        val accessLogFilterConfigurer = AccessLogFilterConfigurer(
            whiteList = emptyList(),
            maxContentLength = 1024,
            enableContentLogging = false
        )
        val accessLogFilter = AccessLogFilter(accessLogFilterConfigurer, defaultAccessLogger!!)

        // When
        accessLogFilter.doFilter(httpServletRequest, httpServletResponse, filterChain!!)

        // Then
        verify(defaultAccessLogger, times(1))!!
            .log(accessLogCaptor!!.capture())

        val accessLog = accessLogCaptor!!.value
        assertThat(accessLog).isNotNull

        // IP 확인
        if (ip.isEmpty() || ip == "unknown")
            assertThat(accessLog.ip).isEqualTo("127.0.0.1")
        else
            assertThat(accessLog.ip).isEqualTo(ip)
    }

    @Test
    @DisplayName("User-Agent 헤더 로깅 테스트")
    fun test_user_agent() {

        // Given
        val httpServletRequest = givenMockHttpServletRequest()
        val httpServletResponse = givenMockHttpServletResponse()

        // User-Agent 헤더 세팅
        val userAgent = "SampleUserAgent"
        httpServletRequest.addHeader("User-Agent", "SampleUserAgent")

        // 최대 contents length
        val accessLogFilterConfigurer = AccessLogFilterConfigurer(
            whiteList = emptyList(),
            maxContentLength = 1024,
            enableContentLogging = false
        )
        val accessLogFilter = AccessLogFilter(accessLogFilterConfigurer, defaultAccessLogger!!)

        // When
        accessLogFilter.doFilter(httpServletRequest, httpServletResponse, filterChain!!)

        // Then
        verify(defaultAccessLogger, times(1))!!
            .log(accessLogCaptor!!.capture())

        val accessLog = accessLogCaptor!!.value
        assertThat(accessLog).isNotNull

        // Request header 확인
        assertThat(accessLog.userAgent).isEqualTo(userAgent)
        assertThat(accessLog.requestHeaders["User-Agent"]).isEqualTo(userAgent)
        assertThat(accessLog.requestHeaders["DeviceClass"]).isNotNull
    }

    private fun givenMockHttpServletRequest(): MockHttpServletRequest {
        val mockHttpServletRequest = MockHttpServletRequest()
        mockHttpServletRequest.method = "post"
        mockHttpServletRequest.requestURI = "/api/v1/foo/bar"
        mockHttpServletRequest.queryString = "?hello=world"
        return mockHttpServletRequest
    }

    @Throws(IOException::class)
    private fun givenMockHttpServletResponse(): MockHttpServletResponse {
        val mockHttpServletResponse = MockHttpServletResponse()
        mockHttpServletResponse.status = 200
        mockHttpServletResponse.setHeader("SAMPLE-RESPONSE-HEADER", "1234567")
        return mockHttpServletResponse
    }
}
