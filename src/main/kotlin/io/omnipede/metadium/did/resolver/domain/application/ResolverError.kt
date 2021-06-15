package io.omnipede.metadium.did.resolver.domain.application

sealed class ResolverError(val reason: String) {
    class NotFoundIdentity(r: String): ResolverError(r)
    class DifferentNetwork(r: String): ResolverError(r)
}
