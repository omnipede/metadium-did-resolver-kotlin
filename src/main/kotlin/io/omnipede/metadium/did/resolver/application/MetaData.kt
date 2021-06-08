package io.omnipede.metadium.did.resolver.application

import java.util.*

/**
 * Metadata of this application
 */
class MetaData(
    val methodMetaData: MethodMetaData,
    val resolverMetaData: ResolverMetaData
) {
    fun markCached() {
        this.resolverMetaData.cached = true
    }

    fun endResolving() {
        this.resolverMetaData.endResolving()
    }
}

/**
 * DID method metadata
 */
data class MethodMetaData(
    val network: String,
    val registryAddress: String,
)

/**
 * Resolver metadata
 */
class ResolverMetaData(
    val driverId: String,
) {
    // Document 캐시 여부
    var cached: Boolean = false
    val driver: String = "HttpDriver"
    val retrieved: Date = Date()
    // Document resolving 하는데 걸린 시간
    var duration: Long = 0L
        private set

    // 측정 시작 시각
    private var resolvingStart: Date = Date()

    /**
     * DID document resolving 시간 측정을 종료하는 메소드
     */
    fun endResolving() {
        this.duration = Date().time - this.resolvingStart.time
    }
}
