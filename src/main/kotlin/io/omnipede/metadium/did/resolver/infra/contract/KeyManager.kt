package io.omnipede.metadium.did.resolver.infra.contract

import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
import io.omnipede.metadium.did.resolver.system.util.toNormalizedHex
import java.util.*
import java.util.stream.Collectors

internal class KeyManager(
    val ownerDID: MetadiumDID,
    val associatedAddresses: List<String>,
    val publicKeyResolvers: List<PublicKeyResolver>,
    val serviceKeyResolvers: List<ServiceKeyResolver>
) {

    private val publicKeySymbol: String = "MetaManagementKey"

    /**
     * Metadium DID 를 이용하여 service key resolver 에 등록된 service key 리스트를 조회하는 메소드
     * @return Service key 리스트
     */
    fun findPublicKeys(): List<PublicKey> {
        // For each associated address,
        return associatedAddresses.parallelStream().map { associatedAddress ->
            val publicKey = PublicKey(did = ownerDID.toString(), keyId = publicKeySymbol, address = associatedAddress)
            // Find public key hex from public key resolver contracts
            val optionalPublicKeyHex = findPublicKeyHex(associatedAddress)
            // If public key hex exists,
            if (optionalPublicKeyHex.isPresent)
                publicKey.publicKeyHex = optionalPublicKeyHex.get()
            publicKey
        }.collect(Collectors.toList())
    }


    /**
     * Public key resolver contract 리스트 상에서 associated address 를 이용해 public key hex 를 찾는 메소드
     * @return Public key hex optional wrapper
     */
    private fun findPublicKeyHex(associatedAddress: String): Optional<String> {

        // 각 contract 을 돌며 확인한다
        for (publicKeyContract in publicKeyResolvers) {
            val publicKeyHexBytes: ByteArray = publicKeyContract.getPublicKey(associatedAddress).send()
            // Byte array to string
            val publicKeyHex = publicKeyHexBytes.joinToString("") {
                "%02x".format(it)
            }.toNormalizedHex()
            // If publicKeyHex is not empty, then set public key hex to publicKey object
            if (publicKeyHex.isNotEmpty())
                return Optional.of(publicKeyHex)
        }
        return Optional.empty()
    }

    /**
     * Metadium DID 를 이용하여 service key resolver 에 등록된 service key 리스트를 조회하는 메소드
     * @return Service key 리스트
     */
    fun findServiceKeys(): List<PublicKey> {
        val ein = ownerDID.ein
        return serviceKeyResolvers.parallelStream().map { serviceKeyContract ->
            val serviceKeyAddresses: List<String> = serviceKeyContract.getKeys(ein).send().filterIsInstance<String>()
            serviceKeyAddresses.parallelStream().map { addr ->
                // Get symbol from contract
                val symbol = serviceKeyContract.getSymbol(addr).send()
                // Create public key
                PublicKey(did=ownerDID.toString(), keyId=symbol, address=addr)
            }.collect(Collectors.toList())
        }.collect(Collectors.toList()).flatten()
    }
}
