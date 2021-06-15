package io.omnipede.metadium.did.resolver.system.filter.accesslog

/**
 * Access log 필터 설정 클래스
 */
class AccessLogFilterConfigurer(
    // 로그를 남기지 않을 URI 리스트
    var whiteList: List<String>? = null,

    // Maximum request, response contents length
    var maxContentLength: Int = 1024,

    // Request, response body 를 로그로 남길지 여부
    var enableContentLogging: Boolean = false,
)
