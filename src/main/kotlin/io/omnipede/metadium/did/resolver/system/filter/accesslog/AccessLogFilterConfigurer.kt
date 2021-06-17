package io.omnipede.metadium.did.resolver.system.filter.accesslog

/**
 * Access log 필터 설정 클래스
 */
class AccessLogFilterConfigurer(
    // 로그를 남기지 않을 URI 리스트
    private var whiteList: List<String> = emptyList(),

    // Maximum request, response contents length
    val maxContentLength: Int = 1024,

    // Request, response body 를 로그로 남길지 여부
    val enableContentLogging: Boolean = false,
) {

    // Maximum content logging size is 1GB
    private val limit = 1024 * 1024 * 1024

    init {
        if (maxContentLength > limit)
            throw IllegalArgumentException("maxContentLength should not be larger than $limit")
    }

    fun isWhiteListed(requestUri: String): Boolean {

        // White list 상에서 request uri 가 존재하는지 확인
        val findResult = whiteList
            .stream().filter { prefix: String? ->
                requestUri.startsWith(prefix!!)
            }
            .findFirst()
            .orElse(null)

        // 존재하면 true 반환
        return findResult != null
    }
}
