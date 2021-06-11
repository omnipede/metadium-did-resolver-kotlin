package io.omnipede.metadium.did.resolver.infra.contract

import arrow.core.Either
import io.omnipede.metadium.did.resolver.domain.ports.ContractService
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
import io.omnipede.metadium.did.resolver.domain.ports.NotFoundIdentityException
import io.omnipede.metadium.did.resolver.system.util.toNormalizedHex
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
internal class MetadiumContractService(
    private val identityRegistry: IdentityRegistry,
    private val publicKeyResolvers: List<PublicKeyResolver>,
    private val serviceKeyResolvers: List<ServiceKeyResolver>,
) : ContractService {

    private val publicKeySymbol: String = "MetaManagementKey"

    /**
     * Metadium DID 와 맵핑되는 public key list 를 반환하는 메소드
     * @param metaDID 대상 DID
     * @return 대상 DID 와 맵핑되는 public key list
     */
    override fun findPublicKeyList(metaDID: MetadiumDID): Either<NotFoundIdentityException, List<PublicKey>> {

        // Find associated address and resolver address of meta DID
        val ( associatedAddresses, identityResolverAddresses ) = findAddressesOfAssociatedAndResolver(metaDID)

        // Find valid contracts using resolver address
        val ( publicKeyContracts, serviceKeyContracts ) = try {
            findValidContracts(identityResolverAddresses)
        } catch (e: NotFoundIdentityException) {
            return Either.Left(e)
        }

        // Create service key list
        val serviceKeyList: List<PublicKey> = findServiceKeyList(metaDID, serviceKeyContracts)

        // For each associated address,
        val publicKeyList: List<PublicKey> = findPublicKeyList(metaDID, associatedAddresses,  publicKeyContracts)

        // ==> Finally get public key object list
        return Either.Right(listOf(serviceKeyList, publicKeyList).flatten())
    }

    /**
     * Metadium DID 에 종속된 identity 상에 존재하는 associated address 와 resolver address 를 찾아 반환하는 메소드
     * @param metadiumDID Associated address 와 resolver address 소유자의 DID
     * @return ( associatedAddresses, identityResolverAddresses )
     */
    private fun findAddressesOfAssociatedAndResolver(metadiumDID: MetadiumDID): Pair<List<String>, List<String>> {
        // Check whether metadium identity exists
        val ein = metadiumDID.ein
        val identityExists = identityRegistry.identityExists(ein)
            .send()

        // if identityExists is false
        if (!identityExists)
            throw NotFoundIdentityException("Not found")

        // Identity tuple consists of following components
        // component (1) is recovery addresses, component (2) is associated addresses,
        // component (3) is providers, component (4) is resolvers
        val identity = identityRegistry.getIdentity(ein).send()

        // Get associated addresses from identity
        val associatedAddresses = identity.component2()

        // if associated addresses is empty, then
        if (associatedAddresses.isEmpty())
            throw NotFoundIdentityException("Deleted meta id")

        // For each identity resolvers
        val identityResolverAddresses = identity.component4()

        return associatedAddresses to identityResolverAddresses
    }

    /**
     * Bean 으로 등록된 public key resolver, service key resolver 중에서 identity 의 resolver address 에
     * 소속된 contract 을 찾아 반환한다. 예를 들어,
     * identity resolver address 가 [ A, B, C, D ] 고 bean 으로 등록된 public key resolver, service key resolver 가 각각 [ B, E ], [ C, F ] 라면
     * 반환하는 값은 [ B ] 와 [ C ] 다.
     * @param identityResolverAddresses Identity 의 resolver address 집합
     * @return Identity resolver address 에 소속된 PublicKeyResolver 와 ServiceKeyResolver contract
     */
    private fun findValidContracts(identityResolverAddresses: List<String>)
    : Pair<List<PublicKeyResolver>, List<ServiceKeyResolver>> {

        val publicKeyResolverContracts: MutableList<PublicKeyResolver> = mutableListOf()
        val serviceKeyResolverContracts: MutableList<ServiceKeyResolver> = mutableListOf()

        // Find public key resolver and find service key resolver
        // (!) Do not use parallel stream below (!)
        identityResolverAddresses.forEach { identityResolverAddress ->
            publicKeyResolverContracts += publicKeyResolvers.parallelStream().filter { publicKeyResolver ->
                publicKeyResolver.contractAddress.toNormalizedHex() != identityResolverAddress.toNormalizedHex()
            }.collect(Collectors.toList())
            serviceKeyResolverContracts += serviceKeyResolvers.parallelStream().filter { serviceKeyResolvers ->
                serviceKeyResolvers.contractAddress.toNormalizedHex() != identityResolverAddress.toNormalizedHex()
            }.collect(Collectors.toList())
        }

        return publicKeyResolverContracts to serviceKeyResolverContracts
    }

    /**
     * Metadium DID 를 이용하여 service key resolver 에 등록된 service key 리스트를 조회하는 메소드
     * @param metadiumDID Service key 소유자의 DID
     * @param serviceKeyResolverContracts Service key 가 등록된 contract 리스트
     * @return Service key 리스트
     */
    private fun findServiceKeyList(metadiumDID: MetadiumDID, serviceKeyResolverContracts: List<ServiceKeyResolver>): List<PublicKey> {
        val ein = metadiumDID.ein
        return serviceKeyResolverContracts.parallelStream().map { serviceKeyContract ->
            val serviceKeyAddresses: List<String> = serviceKeyContract.getKeys(ein).send().filterIsInstance<String>()
            serviceKeyAddresses.parallelStream().map { addr ->
                // Create symbol
                val symbol = serviceKeyContract.getSymbol(addr).send()
                // Create public key
                PublicKey(did=metadiumDID.toString(), keyId=symbol, address=addr)
            }.collect(Collectors.toList())
        }.collect(Collectors.toList()).flatten()
    }

    /**
     * Associated address 를 이용해서 public key resolver 에 등록된 public key 리스트를 조회하는 메소드
     * @param metadiumDID Associated address 소유자의 DID
     * @param associatedAddresses Associated address 리스트
     * @param publicKeyContracts Public key resolver contract 리스트
     * @return Public key 리스트
     */
    private fun findPublicKeyList(metadiumDID: MetadiumDID, associatedAddresses: List<String>, publicKeyContracts: List<PublicKeyResolver>): List<PublicKey> {
        // For each associated address,
        return associatedAddresses.parallelStream().map { associatedAddress ->
            val publicKey = PublicKey(did = metadiumDID.toString(), keyId = publicKeySymbol, address = associatedAddress)
            // Find public key hex from public key resolver contracts
            val optionalPublicKeyHex = findPublicKeyHex(publicKeyContracts, associatedAddress)
            // If public key hex exists,
            if (optionalPublicKeyHex.isPresent)
                publicKey.publicKeyHex = optionalPublicKeyHex.get()
            publicKey
        }.collect(Collectors.toList())
    }

    /**
     * Public key resolver contract 리스트 상에서 associated address 를 이용해 public key hex 를 찾는 메소드
     * @param publicKeyContracts Public key hex 가 존재할지도 모르는 contract 리스트
     * @param associatedAddress Public key hex 를 찾을 때 사용할 address
     * @return Public key hex optional wrapper
     */
    private fun findPublicKeyHex(publicKeyContracts: List<PublicKeyResolver>, associatedAddress: String): Optional<String> {

        // 각 contract 을 돌며 확인한다
        for (publicKeyContract in publicKeyContracts) {
            val publicKeyHexBytes: ByteArray = publicKeyContract.getPublicKey(associatedAddress).send()
            // Byte array to string
            val publicKeyHex = publicKeyHexBytes.joinToString("") {
                "%02x".format(it)
            }.toNormalizedHex()
            if (publicKeyHex.isNotEmpty())
                return Optional.of(publicKeyHex)
        }
        return Optional.empty()
    }
}
