package io.omnipede.metadium.did.resolver.domain

import io.omnipede.metadium.did.resolver.system.util.isValidDid
import java.lang.IllegalArgumentException

/**
 * DID 문자열 클래스
 */
class MetadiumDID(did: String) {

    // DID 접두사 (did:meta)
    private lateinit var prefix: String

    // Network 구분 (mainnet/testnet)
    lateinit var net: String
        private set

    // Metadium ID
    lateinit var metaId: String
        private set

    private val sep: String = ":"

    init {
        if (!did.isValidDid())
            throw IllegalArgumentException("Invalid DID format: $did")

        // ":" 구분자로 나눔
        val words = did.split(sep)

        // 'mainnet' 또는 'testnet' 이 포함되어 있지 않다면 'mainnet' DID 로 가정함
        if (words.size == 3) {
            prefix = words[0] + sep + words[1]
            net = "mainnet"
            metaId = words[2]
        }

        if (words.size == 4) {
            prefix = words[0] + sep + words[1]
            net = words[2]
            metaId = words[3]
        }
    }

    /**
     * prefix, net, metaId 를 join 시켜 원본 DID 를 복구한다
     * @return DID
     */
    override fun toString(): String {
        if (net == "mainnet")
            return listOf(prefix, metaId).joinToString(sep)
        return listOf(prefix, net, metaId).joinToString(sep)
    }
}