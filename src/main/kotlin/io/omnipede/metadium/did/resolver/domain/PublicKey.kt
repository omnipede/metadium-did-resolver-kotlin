package io.omnipede.metadium.did.resolver.domain

import io.omnipede.metadium.did.resolver.system.util.isValidDid
import io.omnipede.metadium.did.resolver.system.util.isValidMetadiumAddress
import io.omnipede.metadium.did.resolver.system.util.isValidMetadiumPublicKeyHex
import java.util.stream.Collectors

/**
 * DID 문서 에 포함될 PublicKey
 */
class PublicKey(did: String, keyId: String, address: String) {

    val type: String = "EcdsaSecp256k1VerificationKey2019"
    val controller: String = did
    val publicKeyHash: String = formatHexString(address)
    val id: String = "$did#$keyId#$publicKeyHash"
    var publicKeyHex: String? = null
        private set

    init {
        // String validation
        if (!did.isValidDid())
            throw IllegalArgumentException("Invalid DID format: $did")
        if (!address.isValidMetadiumAddress())
            throw IllegalArgumentException("Invalid address format: $address")
    }

    constructor(did: String, keyId: String, address: String, publicKey: String): this(did, keyId, address) {
        // String validation
        if (!publicKey.isValidMetadiumPublicKeyHex())
            throw IllegalArgumentException("Invalid public key hex format: $publicKey")
        // Address 에서 0x prefix 를 삭제하고 lower case 로 만든다.
        this.publicKeyHex = formatHexString(publicKey)
    }

    /**
     * 문자열의 '0x' prefix 를 삭제하고 lower case 로 만든다.
     * @param s 대상 문자열
     * @return 0x prefix 가 제거된 lower case 문자열
     */
    private fun formatHexString(s: String): String {
        return s.removePrefix("0x")
            .removePrefix("0X")
            .toList().stream().parallel().map {
                it.toLowerCase()
            }.collect(Collectors.toList()).joinToString("")
    }
}
