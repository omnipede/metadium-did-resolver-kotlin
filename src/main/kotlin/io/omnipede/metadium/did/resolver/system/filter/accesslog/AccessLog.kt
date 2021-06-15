package io.omnipede.metadium.did.resolver.system.filter.accesslog

import java.util.*

data class AccessLog(
    // 요청 시각
    val requestAt: Date,

    // 응답 시각
    val responseAt: Date,

    // User agent
    val userAgent: String,
    val hostName: String,
    val ip: String,
    val uri: String,
    val query: String?,
    val method: String,
    val requestHeaders: Map<String, String>,
    var requestBody: String?,
    val status: Int,
    val responseHeaders: Map<String, String>,
    var responseBody: String?,
    val elapsed: Long
)
