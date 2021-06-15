package io.omnipede.metadium.did.resolver.domain.entity

import io.omnipede.metadium.did.resolver.system.util.isValidDid
import java.util.stream.Collectors

/**
 * DID document DS
 */
class DidDocument(did: String) {
    val context = "https://w3id.org/did/v0.11"
    val id = did
    var publicKeyList: List<PublicKey>
        private set
    var authenticationList: List<String>
        private set
    var associatedServiceList: List<AssociatedService>
        private set

    init {
        // String validation
        if (!id.isValidDid())
            throw IllegalArgumentException("Invalid DID format: $did")
        publicKeyList = emptyList()
        authenticationList = emptyList()
        associatedServiceList = emptyList()
    }

    constructor(did: String, publicKeyList: List<PublicKey>): this(did) {
        this.publicKeyList = publicKeyList
        // PublicKey 의 ID 만 따로 모아서 authentication list 를 새로 만든다
        this.authenticationList = this.publicKeyList
            .stream().parallel().map { it.id }.collect(Collectors.toList())
    }

    constructor(did: String, publicKeyList: List<PublicKey>, associatedServiceList: List<AssociatedService>): this(did, publicKeyList) {
        this.associatedServiceList = associatedServiceList
    }
}
