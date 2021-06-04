package io.omnipede.metadium.did.resolver.domain

import io.omnipede.metadium.did.resolver.system.util.isValidDid
import java.util.stream.Collectors

/**
 * DID document DS
 */
class DidDocument(did: String) {
    val context = "https://w3id.org/did/v0.11"
    val id = did
    val publicKeyList: MutableList<PublicKey> = mutableListOf()
    val authenticationList: MutableList<String> = mutableListOf()
    val serviceList: MutableList<Service> = mutableListOf()

    init {
        // String validation
        if (!id.isValidDid())
            throw IllegalArgumentException("Invalid DID format: $did")
    }

    constructor(did: String, publicKeyList: List<PublicKey>): this(did) {
        this.publicKeyList += publicKeyList
        // PublicKey 의 ID 만 따로 모아서 authentication list 를 새로 만든다
        this.authenticationList += this.publicKeyList
            .stream().parallel().map { it.id }.collect(Collectors.toList())
    }

    constructor(did: String, publicKeyList: List<PublicKey>, serviceList: List<Service>): this(did, publicKeyList) {
        this.serviceList += serviceList
    }
}