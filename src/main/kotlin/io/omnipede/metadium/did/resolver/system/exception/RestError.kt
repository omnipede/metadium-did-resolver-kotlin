package io.omnipede.metadium.did.resolver.system.exception

internal data class RestError(
    val status: Int,
    val message: String
)
