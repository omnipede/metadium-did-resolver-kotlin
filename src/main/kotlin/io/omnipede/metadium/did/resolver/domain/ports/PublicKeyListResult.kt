package io.omnipede.metadium.did.resolver.domain.ports

import io.omnipede.metadium.did.resolver.domain.entity.PublicKey

class PublicKeyListResult(
    val publicKeyList: List<PublicKey>,
    val serviceKeyList: List<PublicKey>
) {
    fun toList(): List<PublicKey> {
        return listOf(publicKeyList, serviceKeyList).flatten()
    }
}
