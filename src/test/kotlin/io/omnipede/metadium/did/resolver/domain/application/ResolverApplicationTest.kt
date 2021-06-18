package io.omnipede.metadium.did.resolver.domain.application

import arrow.core.Either
import io.omnipede.metadium.did.resolver.domain.entity.*
import io.omnipede.metadium.did.resolver.domain.ports.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*
import java.util.stream.Stream

internal class ResolverApplicationTest {

    private var contractService: ContractService? = null
    private var envService: EnvService? = null
    private var documentCache: DocumentCache? = null
    private var resolverApplication: ResolverApplication? = null

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @BeforeEach
    fun setup() {
        contractService = mock(ContractService::class.java)
        envService = mock(EnvService::class.java)
        documentCache = mock(DocumentCache::class.java)
        resolverApplication = ResolverApplication(contractService!!, envService!!, documentCache!!)
    }

    class ResolverApplicationTestArgumentProvider: ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
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

    @Nested
    inner class ResolveTest {

        @ParameterizedTest(name = "DID document 와 metadata 가 반환되어야 한다: {index}")
        @ArgumentsSource(ResolverApplicationTestArgumentProvider::class)
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

            doReturn(Optional.empty<DidDocument>())
                .`when`(documentCache)!!
                .find(any(MetadiumDID::class.java))

            doReturn(Either.Right(PublicKeyListResult(metaManagementKeyList, serviceKeyList)))
                .`when`(contractService)!!
                .findPublicKeyList(any(MetadiumDID::class.java))

            // When
            val result = resolverApplication!!.resolve(did, false)

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

                assertThat(metadata.resolverMetaData.cached).isFalse

                // Meta management key 가 하나 이상일 때 associated service 가 생성되어야 한다
                if (metaManagementKeyList.isNotEmpty())
                    verify(envService)!!.createService(metaManagementKeyList[0])

                // 캐시되어야 한다
                verify(documentCache)!!.save(any(DidDocument::class.java))
            }
        }

        @ParameterizedTest(name = "Document 가 캐시되어있을 때 캐시된 document 를 반환한다")
        @ArgumentsSource(ResolverApplicationTestArgumentProvider::class)
        fun should_return_cached_document_when_document_is_cached(metaManagementKeyList: List<PublicKey>, serviceKeyList: List<PublicKey>) {

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

            val cachedDocument = DidDocument(
                did = MetadiumDID(did).toString(),
                publicKeyList = listOf(metaManagementKeyList, serviceKeyList).flatten()
            )

            doReturn(Optional.of<DidDocument>(cachedDocument))
                .`when`(documentCache)!!
                .find(any(MetadiumDID::class.java))

            doReturn(Either.Right(PublicKeyListResult(metaManagementKeyList, serviceKeyList)))
                .`when`(contractService)!!
                .findPublicKeyList(any(MetadiumDID::class.java))

            // When
            val result = resolverApplication!!.resolve(did, false)

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
                assertThat(metadata.resolverMetaData.cached).isTrue
                // Contract 호출은 일어나선 안된다
                verify(contractService, times(0))!!
                    .findPublicKeyList(any(MetadiumDID::class.java))
            }
        }

        @ParameterizedTest(name = "noCache 파라미터값이 true 일 때 캐시를 조회하지 않는다")
        @ArgumentsSource(ResolverApplicationTestArgumentProvider::class)
        fun should_find_document_from_contract_when_noCache_is_true(metaManagementKeyList: List<PublicKey>, serviceKeyList: List<PublicKey>) {

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
                .findPublicKeyList(any(MetadiumDID::class.java))

            // When
            val result = resolverApplication!!.resolve(did, true)

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

                assertThat(metadata.resolverMetaData.cached).isFalse

                // Meta management key 가 하나 이상일 때 associated service 가 생성되어야 한다
                if (metaManagementKeyList.isNotEmpty())
                    verify(envService)!!.createService(metaManagementKeyList[0])

                // 캐시를 조회하면 안된다
                verify(documentCache, times(0))!!.find(any(MetadiumDID::class.java))

                // 캐시되어야 한다
                verify(documentCache, times(1))!!.save(any(DidDocument::class.java))
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
            val result = resolverApplication!!.resolve(did, false)

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
                .findPublicKeyList(any(MetadiumDID::class.java))

            // When
            val result = resolverApplication!!.resolve(did, false)

            // Then
            assertThat(result.isLeft()).isTrue
            result.mapLeft {
                assertThat(it).isInstanceOf(ResolverError.NotFoundIdentity::class.java)
                assertThat(it.reason).isEqualTo(message)
            }
        }
    }

    @Nested
    inner class DeleteCachedDocumentTest {

        @Test
        @DisplayName("캐시된 document 를 지울 수 있어야 한다")
        fun should_delete_cached_document() {

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

            doReturn(true)
                .`when`(documentCache)!!
                .delete(any(MetadiumDID::class.java))

            // When
            val result = resolverApplication!!.deleteDocumentFromCache(did)

            // Then
            assertThat(result.isRight()).isTrue
            result.map {
                assertThat(it).isNotNull
                assertThat(it).isEqualTo(true)
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
            val result = resolverApplication!!.deleteDocumentFromCache(did)

            // Then
            assertThat(result.isLeft()).isTrue
            result.mapLeft {
                assertThat(it).isInstanceOf(ResolverError.DifferentNetwork::class.java)
                assertThat(it.reason).isEqualTo("This server is DID resolver for $network")
            }
        }
    }
}
