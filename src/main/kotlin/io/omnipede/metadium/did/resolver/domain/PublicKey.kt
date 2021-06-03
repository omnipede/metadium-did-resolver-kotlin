package io.omnipede.metadium.did.resolver.domain

/**
 * DID 문서 에 포함될 PublicKey
 */
class PublicKey(did: String, keyId: String, address: String) {

    val type: String = "EcdsaSecp256k1VerificationKey2019"
    val controller: String = did
    val publicKeyHash: String = formatHexString(address)
    val id: String = "$did#$keyId#$publicKeyHash"
    var publicKeyHex: String? = null

    // Address / publicKeyHex validation 용 Regex
    private val addressRegex: Regex = Regex("^(0x|0X)?[0-9a-fA-F]{40}$")

    init {
        // String validation
        if (!did.matches(Regex("^did:meta:((testnet|mainnet):)?[0-9a-f]{64}$")))
            throw IllegalArgumentException("Invalid DID format: $did")
        if (!address.matches(addressRegex))
            throw IllegalArgumentException("Invalid address format: $address")
    }

    constructor(did: String, keyId: String, address: String, publicKey: String): this(did, keyId, address) {
        // String validation
        if (!publicKey.matches(addressRegex))
            throw IllegalArgumentException("Invalid public key hex format: $publicKey")
        // Address 에서 0x prefix 를 삭제하고 lower case 로 만든다.
        val formattedPublicKey = formatHexString(publicKey)
        this.publicKeyHex = formattedPublicKey
    }

    /**
     * 문자열의 '0x' prefix 를 삭제하고 lower case 로 만든다.
     * @param s 대상 문자열
     * @return 0x prefix 가 제거된 lower case 문자열
     */
    private fun formatHexString(s: String): String {
         return if (s.startsWith("0x") || s.startsWith("0X"))
            s.substring(2).toLowerCase() else s.toLowerCase()
    }
}