package io.omnipede.metadium.did.resolver.domain.application

sealed class ResolverError(val reason: String) {
    data class NotFoundIdentity(val r: String): ResolverError(r)
    data class DifferentNetwork(val r: String): ResolverError(r)
}
