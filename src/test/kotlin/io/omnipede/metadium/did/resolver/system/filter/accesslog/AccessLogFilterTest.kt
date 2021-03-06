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

                // Request body, response body ??? ?????? ??? ???
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

    @ParameterizedTest(name = "Request/response body ?????? ?????????: {index}")
    @MethodSource("test_content_logging_argumentsProvider")
    fun test_content_logging(requestBody: String?, responseBody: String?) {

        // Given
        val httpServletRequest = givenMockHttpServletRequest()
        httpServletRequest.setContent(requestBody?.toByteArray())

        val httpServletResponse = givenMockHttpServletResponse()

        if (responseBody != null) {
            // filterChain.doFilter ??????????????? ??? ?????? argument ??? CachingResponseWrapper ??? ??????????????? mocking ???
            doAnswer { invocationOnMock: InvocationOnMock ->

                // ?????????????????? getReader() ???????????? request body ??? ????????????
                val requestWrapper =
                    invocationOnMock.arguments[0] as HttpServletRequest
                val inputStream = requestWrapper.inputStream

                // ?????????????????? getInputStream() ???????????? request body ??? ????????????
                val content = IOUtils.toString(inputStream)
                if (requestBody != null)
                    assertThat(content).isEqualTo(requestBody)

                // ?????????????????? output stream ??? response body ??? ??????
                val responseWrapper =
                    invocationOnMock.arguments[1] as HttpServletResponse
                val outputStream: OutputStream = responseWrapper.outputStream
                outputStream.write(responseBody.toByteArray())
            }
                .`when`(filterChain)!!
                .doFilter(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java))
        }

        // ?????? contents length
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

        // Request body ??????
        when {
            requestBody == null -> assertThat(accessLog.requestBody).isEqualTo("")
            requestBody.length > maxContentsLength -> assertThat(accessLog.requestBody).isEqualTo("TOO LONG CONTENTS")
            else -> assertThat(accessLog.requestBody).isEqualTo(requestBody)
        }

        // Response body ??????
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

        // Request header ??????
        val requestHeaders = Collections.list(httpServletRequest.headerNames).associateWith { headerName ->
            httpServletRequest.getHeader(headerName)
        }
        assertThat(accessLog.requestHeaders.filterKeys { it != "DeviceClass" }).isEqualTo(requestHeaders)

        // Response header ??????
        val responseHeaders = httpServletResponse.headerNames.associateWith { headerName ->
            httpServletResponse.getHeader(headerName)
        }
        assertThat(accessLog.responseHeaders).isEqualTo(responseHeaders)
    }

    @Test
    @DisplayName("Whitelist ??? URI ??? ?????? access log ??? ???????????? ?????????")
    fun test_whiteListing() {

        // Given
        val requestBody = "Hello world"
        val requestUri = "/api/v1/temp"
        val httpServletRequest = givenMockHttpServletRequest()
        httpServletRequest.requestURI = requestUri
        httpServletRequest.setContent(requestBody.toByteArray())

        val httpServletResponse = givenMockHttpServletResponse()

        // AccessLog configurer ??????
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

    @DisplayName("enableContentLogging = false ??? ?????? content ??? ???????????? ?????????")
    @Test
    fun test_disable_content_logging() {

        // Given
        val requestBody = "Hello world"
        val responseBody = "Hello response"

        val httpServletRequest = givenMockHttpServletRequest()
        httpServletRequest.setContent(requestBody.toByteArray())

        val httpServletResponse = givenMockHttpServletResponse()

        // filterChain.doFilter ??????????????? ??? ?????? argument ??? CachingResponseWrapper ??? ??????????????? mocking ???
        doAnswer { invocationOnMock: InvocationOnMock ->

            // ?????????????????? output stream ??? response body ??? ??????
            val responseWrapper =
                invocationOnMock.arguments[1] as HttpServletResponse
            val outputStream: OutputStream = responseWrapper.outputStream
            outputStream.write(responseBody.toByteArray())
        }
            .`when`(filterChain)!!
            .doFilter(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java))

        // ?????? contents length
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

        // Request body ??????
        assertThat(accessLog.requestBody).isNull()

        // Response body ??????
        assertThat(accessLog.responseBody).isNull()

        assertThat(accessLog.method).isEqualTo(httpServletRequest.method)
        assertThat(accessLog.uri).isEqualTo(httpServletRequest.requestURI)
        assertThat(accessLog.query).isEqualTo(httpServletRequest.queryString)
        assertThat(accessLog.requestAt).isBefore(Date())
        assertThat(accessLog.responseAt).isBefore(Date())
        assertThat(accessLog.status).isEqualTo(httpServletResponse.status)
        assertThat(accessLog.userAgent).isEqualTo("Unknown")
        assertThat(accessLog.elapsed).isLessThan(duration)

        // Request header ??????
        val requestHeaders = Collections.list(httpServletRequest.headerNames).associateWith { headerName ->
            httpServletRequest.getHeader(headerName)
        }
        assertThat(accessLog.requestHeaders.filterKeys { it != "DeviceClass" }).isEqualTo(requestHeaders)

        // Response header ??????
        val responseHeaders = httpServletResponse.headerNames.associateWith { headerName ->
            httpServletResponse.getHeader(headerName)
        }
        assertThat(accessLog.responseHeaders).isEqualTo(responseHeaders)
    }

    @ParameterizedTest(name = "?????? request header ??? ip ??? ??????????????? ??? ????????? ??????????????? ????????????: {0}, {1}")
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

        // ?????? contents length
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

        // IP ??????
        if (ip.isEmpty() || ip == "unknown")
            assertThat(accessLog.ip).isEqualTo("127.0.0.1")
        else
            assertThat(accessLog.ip).isEqualTo(ip)
    }

    @Test
    @DisplayName("User-Agent ?????? ?????? ?????????")
    fun test_user_agent() {

        // Given
        val httpServletRequest = givenMockHttpServletRequest()
        val httpServletResponse = givenMockHttpServletResponse()

        // User-Agent ?????? ??????
        val userAgent = "SampleUserAgent"
        httpServletRequest.addHeader("User-Agent", "SampleUserAgent")

        // ?????? contents length
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

        // Request header ??????
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
