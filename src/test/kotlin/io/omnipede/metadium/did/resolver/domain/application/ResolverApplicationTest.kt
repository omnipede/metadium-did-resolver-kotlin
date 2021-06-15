package io.omnipede.metadium.did.resolver.domain.application

import arrow.core.Either
import io.omnipede.metadium.did.resolver.domain.entity.AssociatedService
import io.omnipede.metadium.did.resolver.domain.entity.AssociatedServiceTest
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
import io.omnipede.metadium.did.resolver.domain.ports.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*
import java.util.stream.Stream

internal class ResolverApplicationTest {

    private var contractService: ContractService? = null
    private var envService: EnvService? = null
    private var resolverApplication: ResolverApplication? = null

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @BeforeEach
    fun setup() {
        contractService = mock(ContractService::class.java)
        envService = mock(EnvService::class.java)
        resolverApplication = ResolverApplication(contractService!!, envService!!)
    }

    companion object {

        /**
         * Argument provider for parameterized test
         */
        @JvmStatic
        fun argumentProvider(): Stream<Arguments>? {
            val did = "did:meta:mainnet:000000000000000000000000000000000000000000000000000000000000112b"
            val publicKey = PublicKey(did, "MetaManagementKey", "0x0C65a336fc97d4cf830baeb739153f312cbefcc9")
            val serviceKey = PublicKey(did, UUID.randomUUID().toString(), "0x0C65a336fc97d4cf830baeb739153f312cbefcc9")
            return Stream.of(
                // (MetaManagementKey, ServiceKey)
                Arguments.of(
                    listOf(publicKey), listOf(serviceKey)
                ),
                Arguments.of(
                    emptyList<PublicKey>(), listOf(serviceKey)
                ),
                Arguments.of(
                    listOf(publicKey, publicKey), listOf(serviceKey)
                )
            )
        }
    }

    @ParameterizedTest(name = "DID document 와 metadata 가 반환되어야 한다: {index}")
    @MethodSource("argumentProvider")
    fun should_return_document_and_metadata(metaManagementKeyList: List<PublicKey>, serviceKeyList: List<PublicKey>) {

        // Given
        val did = "did:meta:mainnet:000000000000000000000000000000000000000000000000000000000000112b"
        val network = "mainnet"
        val registryAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
        val driverId = "did-meta"

        doReturn(MetaData(
            methodMetaData = MethodMetaData(network, registryAddress),
            resolverMetaData = ResolverMetaData(driverId)
        )).`when`(envService)!!
            .loadMetaData()

        doReturn(Either.Right(PublicKeyListResult(metaManagementKeyList, serviceKeyList)))
            .`when`(contractService)!!
            .findPublicKeyList(this.any(MetadiumDID::class.java))

        // When
        val result = resolverApplication!!.resolve(did)

        // Then
        assertThat(result.isRight()).isTrue
        result.map {
            assertThat(it).isNotNull
            val (document, metadata) = it
            assertThat(document).isNotNull
            assertThat(metadata).isNotNull
            assertThat(document.id).isEqualTo("did:meta:000000000000000000000000000000000000000000000000000000000000112b")
            assertThat(document.publicKeyList.size).isEqualTo(
                listOf(metaManagementKeyList, serviceKeyList).flatten().size
            )
            assertThat(document.publicKeyList).containsExactlyInAnyOrderElementsOf(
                listOf(metaManagementKeyList, serviceKeyList).flatten()
            )

            // Meta management key 가 하나 이상일 때 associated service 가 생성되어야 한다
            if (metaManagementKeyList.isNotEmpty())
                verify(envService)!!.createService(metaManagementKeyList[0])
        }
    }

    @Test
    @DisplayName("DID network 가 상이할 때 에러를 반환해야 한다")
    fun should_return_error_when_network_is_different() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val network = "testnet"
        val registryAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
        val driverId = "did-meta"

        doReturn(MetaData(
            methodMetaData = MethodMetaData(network, registryAddress),
            resolverMetaData = ResolverMetaData(driverId)
        )).`when`(envService)!!
            .loadMetaData()

        // When
        val result = resolverApplication!!.resolve(did)

        // Then
        assertThat(result.isLeft()).isTrue
        result.mapLeft {
            assertThat(it).isInstanceOf(ResolverError.DifferentNetwork::class.java)
            assertThat(it.reason).isEqualTo("This server is DID resolver for $network")
        }
    }

    @Test
    @DisplayName("Identity 가 존재하지 않을 때 에러를 반환해야 한다")
    fun should_return_error_when_identity_is_not_found() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val network = "mainnet"
        val registryAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
        val driverId = "did-meta"

        doReturn(MetaData(
            methodMetaData = MethodMetaData(network, registryAddress),
            resolverMetaData = ResolverMetaData(driverId)
        )).`when`(envService)!!
            .loadMetaData()

        val message = "Not found identity!!"
        doReturn(Either.Left(NotFoundIdentityException(message)))
            .`when`(contractService)!!
            .findPublicKeyList(this.any(MetadiumDID::class.java))

        // When
        val result = resolverApplication!!.resolve(did)

        // Then
        assertThat(result.isLeft()).isTrue
        result.mapLeft {
            assertThat(it).isInstanceOf(ResolverError.NotFoundIdentity::class.java)
            assertThat(it.reason).isEqualTo(message)
        }
    }
}
