package io.omnipede.metadium.did.resolver.system.exception

internal data class RestError(
    val message: String
) {
    val success: Boolean = false
}
