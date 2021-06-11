package io.omnipede.metadium.did.resolver.domain.entity

import io.omnipede.metadium.did.resolver.system.util.isValidDid
import io.omnipede.metadium.did.resolver.system.util.isValidWebUrl

/**
 * Service DS
 */
class AssociatedService(did: String, publicKey: PublicKey, url: String) {

    val id = did
    val publicKey = publicKey.id
    val type = "identityHub"
    val serviceEndpoint = url

    init {
        // String validation
        if (!did.isValidDid())
            throw IllegalArgumentException("Invalid DID format: $did")
        if (!serviceEndpoint.isValidWebUrl())
            throw IllegalArgumentException("Invalid URL format: $url")
    }
}
