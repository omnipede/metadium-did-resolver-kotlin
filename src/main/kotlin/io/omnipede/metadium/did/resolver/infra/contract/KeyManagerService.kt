package io.omnipede.metadium.did.resolver.infra.contract

import arrow.core.Either
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.ports.NotFoundIdentityException
import io.omnipede.metadium.did.resolver.system.util.toNormalizedHex
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
internal class KeyManagerService(
    private val identityRegistry: IdentityRegistry,
    private val publicKeyResolvers: List<PublicKeyResolver>,
    private val serviceKeyResolvers: List<ServiceKeyResolver>
) {

    /**
     * Metadium DID 를 이용해서 identity 를 찾고, identity 정보를 이용해서 key manager 객체를 생성하는 메소드
     * @param metadiumDID Metadium DID
     * @return Key manager
     */
    fun createKeyManager(metadiumDID: MetadiumDID): Either<NotFoundIdentityException, KeyManager> {
        // Find associated address and resolver address of meta DID
        val ( associatedAddresses, identityResolverAddresses ) = try {
            findAddressesOfAssociatedAndResolver(metadiumDID)
        } catch (e: NotFoundIdentityException) {
            return Either.Left(e)
        }

        // Find valid contracts using resolver address
        val ( publicKeyContracts, serviceKeyContracts ) = findValidContracts(identityResolverAddresses)

        return Either.Right(
            KeyManager(metadiumDID, associatedAddresses, publicKeyContracts, serviceKeyContracts)
        )
    }

    /**
     * Metadium DID 에 종속된 identity 상에 존재하는 associated address 와 resolver address 를 찾아 반환하는 메소드
     * @param metadiumDID Associated address 와 resolver address 소유자의 DID
     * @return ( associatedAddresses, identityResolverAddresses )
     */
    private fun findAddressesOfAssociatedAndResolver(metadiumDID: MetadiumDID): Pair<MutableList<String>, MutableList<String>> {
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

        // Identity resolver address 리스트에 contract address 가 존재하는 contract 만 필터링한다
        val publicKeyResolverContracts = publicKeyResolvers.parallelStream().filter {
            val contractAddress = it.contractAddress.toNormalizedHex()
            identityResolverAddresses.any { resolverAddress -> resolverAddress.toNormalizedHex() == contractAddress }
        }.collect(Collectors.toList())

        val serviceKeyResolverContracts = serviceKeyResolvers.parallelStream().filter {
            val contractAddress = it.contractAddress.toNormalizedHex()
            identityResolverAddresses.any { resolverAddress -> resolverAddress.toNormalizedHex() == contractAddress }
        }.collect(Collectors.toList())

        return publicKeyResolverContracts to serviceKeyResolverContracts
    }
}
