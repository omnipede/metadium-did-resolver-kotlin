package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*
import java.util.function.Consumer
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.LinkedHashMap


/**
 * Access log 를 남기는 필터.
 */
class AccessLogFilter(
    // Access log filter configuration
    private var accessLogFilterConfigurer: AccessLogFilterConfigurer,
    // 실제 access log 를 남길 시 사용하는 인터페이스
    private var accessLogger: DefaultAccessLogger
) : OncePerRequestFilter() {

    // User agent 분석 시 사용하는 객체
    private val userAgentService: UserAgentService = UserAgentService()

    // 서버 host name
    private val hostName = HostName()

    /**
     * Normal servlet filter method.
     */
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {

        // White list 된 request URI 일 경우 pass 처리
        if (accessLogFilterConfigurer.isWhiteListed(httpServletRequest.requestURI)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse)
            return
        }

        // IF enableContentCapture is true
        // request, response body 를 로그에 남김
        if (accessLogFilterConfigurer.enableContentLogging) {
            processAccessLogWithContents(httpServletRequest, httpServletResponse, filterChain)
            return
        }

        // Else, request response body 를 로그에 남기지 않음
        processAccessLogWithoutContents(httpServletRequest, httpServletResponse, filterChain)
    }

    /**
     * Contents (request & response body) 를 로그에 남기는 메소드
     */
    @Throws(IOException::class, ServletException::class)
    private fun processAccessLogWithContents(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Request body caching
        val requestWrapper = CachingRequestWrapper(httpServletRequest)
        val responseWrapper = CachingResponseWrapper(httpServletResponse)

        // 요청 시각
        val requestAt = Date()

        // Response body caching
        filterChain.doFilter(requestWrapper, responseWrapper)

        // 응답 시각
        val responseAt = Date()
        val accessLog = createAccessLog(requestWrapper, responseWrapper, requestAt, responseAt)

        // Body 추출
        val requestBody = requestWrapper.getBody(accessLogFilterConfigurer.maxContentLength)
        val responseBody = responseWrapper.getBody(accessLogFilterConfigurer.maxContentLength)

        // Access 로그에 body 추가
        accessLog.requestBody = requestBody
        accessLog.responseBody = responseBody

        // 로그 남기기
        accessLogger.log(accessLog)
    }

    /**
     * Contents (request, response body) 를 로그에 남기지 않음
     */
    @Throws(IOException::class, ServletException::class)
    private fun processAccessLogWithoutContents(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 요청 시각
        val requestAt = Date()
        filterChain.doFilter(httpServletRequest, httpServletResponse)
        // 응답 시각
        val responseAt = Date()
        val accessLog = createAccessLog(httpServletRequest, httpServletResponse, requestAt, responseAt)
        // 로그 남기기
        accessLogger.log(accessLog)
    }

    /**
     * Servlet request, servlet response 상에서 로깅할 정보를 추출하는 메소드
     * @param httpServletRequest Servlet request
     * @param httpServletResponse Servlet response
     * @param requestAt 요청 시각
     * @param responseAt 응답 시각
     */
    private fun createAccessLog(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        requestAt: Date,
        responseAt: Date
    ): AccessLog {
        // 소요 시간 측정
        val elapsed: Long = responseAt.time - requestAt.time

        // User-Agent 분석
        var userAgent = httpServletRequest.getHeader("User-Agent")
        if (userAgent == null) userAgent = "Unknown"
        val deviceClass: String = userAgentService.getDeviceClass(userAgent)

        // Request
        // IP address
        val ip = extractIpAddress(httpServletRequest)
        // URI
        val uri = httpServletRequest.requestURI
        // URL query part
        val query = httpServletRequest.queryString

        // METHOD
        val method = httpServletRequest.method
        // Request headers
        val requestHeaders = getMapOfRequestHeaders(httpServletRequest)
        requestHeaders["DeviceClass"] = deviceClass
        // Response STATUS
        val httpStatus = httpServletResponse.status
        // Response headers
        val responseHeaders = getMapOfResponseHeaders(httpServletResponse)

        // Create access log object
        return AccessLog(
            requestAt = requestAt,
            responseAt = responseAt,
            userAgent = userAgent,
            hostName = hostName.hostName,
            ip = ip,
            uri = uri,
            query = query,
            method = method,
            requestHeaders = requestHeaders,
            status = httpStatus,
            responseHeaders = responseHeaders,
            elapsed = elapsed,
            requestBody = null,
            responseBody = null
        )
    }

    /**
     * Servlet request 의 헤더에서 IP 주소를 추출하는 메소드
     * @param httpServletRequest IP 주소를 추출할 servlet request
     * @return IP 주소
     */
    private fun extractIpAddress(httpServletRequest: HttpServletRequest): String {

        // 요청이 proxy 되었을 때 다음 헤더 중 하나에 원본 client ip 가 존재한다.
        val xForwardedFor = httpServletRequest.getHeader("X-Forwarded-For")
        if (isValidIp(xForwardedFor)) return xForwardedFor

        val proxyClientIp = httpServletRequest.getHeader("Proxy-Client-IP")
        if (isValidIp(proxyClientIp)) return proxyClientIp

        val wlProxyClientIp = httpServletRequest.getHeader("WL-Proxy-Client-IP")
        if (isValidIp(wlProxyClientIp)) return wlProxyClientIp

        val httpClientIp = httpServletRequest.getHeader("HTTP_CLIENT_IP")
        if (isValidIp(httpClientIp)) return httpClientIp

        val httpXForwardedFor = httpServletRequest.getHeader("HTTP_X_FORWARDED_FOR")
        return if (isValidIp(httpXForwardedFor)) httpXForwardedFor

        // 그 외의 경우, proxy 되지 않았다고 판단하여 getRemoteAddr() 메소드로 IP 주소를 가져옴.
        else
            httpServletRequest.remoteAddr
    }

    /**
     * 대상 IP 문자열이 valid ip string 인지 확인하는 메소드
     * @param target 확인할 문자열
     * @return IP address validity
     */
    private fun isValidIp(target: String?): Boolean {
        return if (target == null || target.isEmpty()) false else !"unknown".equals(target, ignoreCase = true)
    }

    /**
     * Servlet request 의 헤더를 Map 형태로 변환하는 메소드
     * @param httpServletRequest 헤더를 포함하는 servlet request
     * @return Map 형태의 헤더
     */
    private fun getMapOfRequestHeaders(httpServletRequest: HttpServletRequest): MutableMap<String, String> {
        val headers: MutableMap<String, String> = LinkedHashMap()
        val headerNames = httpServletRequest.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            headers[headerName] = httpServletRequest.getHeader(headerName)
        }
        return headers
    }

    /**
     * Servlet response 의 헤더를 Map 형태로 변환하는 메소드
     * @param httpServletResponse 헤더를 포함하는 servlet response
     * @return Map 형태의 헤더
     */
    private fun getMapOfResponseHeaders(httpServletResponse: HttpServletResponse): Map<String, String> {
        val headers: MutableMap<String, String> = LinkedHashMap()
        httpServletResponse.headerNames.forEach(Consumer { headerName: String ->
            headers[headerName] = httpServletResponse.getHeader(headerName)
        })
        return headers
    }
}
