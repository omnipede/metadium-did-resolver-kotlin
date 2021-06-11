package io.omnipede.metadium.did.resolver.domain.entity

import io.omnipede.metadium.did.resolver.system.util.*
import io.omnipede.metadium.did.resolver.system.util.isValidDid
import io.omnipede.metadium.did.resolver.system.util.isValidMetadiumAddress
import io.omnipede.metadium.did.resolver.system.util.isValidMetadiumPublicKeyHex

/**
 * DID 문서 에 포함될 PublicKey
 */
class PublicKey(did: String, keyId: String, address: String) {

    val type: String = "EcdsaSecp256k1VerificationKey2019"
    val controller: String = did
    var publicKeyHash: String? = address.toNormalizedHex()
        private set
    val id: String = "$did#$keyId#$publicKeyHash"

    // Public key hex 를 세팅할 경우, public key hash 는 null 이 되어야 한다
    var publicKeyHex: String? = null
        set(value) {
            // String validation
            value?.let {
                if(!it.isValidMetadiumPublicKeyHex())
                    throw IllegalArgumentException("Invalid public key hex format: $field")
                publicKeyHash = null

                // "04" 를 앞에 붙여준다
                field = "04" + value.toNormalizedHex()
            }
        }

    init {
        // String validation
        if (!did.isValidDid())
            throw IllegalArgumentException("Invalid DID format: $did")
        if (!address.isValidMetadiumAddress())
            throw IllegalArgumentException("Invalid address format: $address")
    }
}
