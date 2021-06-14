package io.omnipede.metadium.did.resolver.infra.contract

import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.ports.NotFoundIdentityException
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.web3j.protocol.core.RemoteFunctionCall
import org.web3j.tuples.generated.Tuple4
import java.util.stream.Collectors
import java.util.stream.Stream

internal class KeyManagerServiceTest {

    private var keyManagerService: KeyManagerService? = null
    private var identityRegistry: IdentityRegistry? = null
    private var publicKeyResolvers: MutableList<PublicKeyResolver>? = null
    private var serviceKeyResolvers: MutableList<ServiceKeyResolver>? = null

    @BeforeEach
    fun setup() {
        identityRegistry = mock(IdentityRegistry::class.java)
        publicKeyResolvers = mutableListOf()
        serviceKeyResolvers = mutableListOf()
    }

    @ParameterizedTest(name = "KeyManager 객체 생성 테스트: {index}")
    @MethodSource("should_return_keyManager_argumentsProvider")
    fun should_return_keyManager(
        associatedAddresses: List<String>, resolverAddresses: List<String>,
        mockPublicKeyResolvers: List<PublicKeyResolver>, mockServiceKeyResolvers: List<ServiceKeyResolver>
    ) {
        // Given
        val metadiumDID = MetadiumDID("did:meta:000000000000000000000000000000000000000000000000000000000000112b")

        val identityExistsWrapper = mock(RemoteFunctionCall::class.java)
        doReturn(true)
            .`when`(identityExistsWrapper)
            .send()

        doReturn(identityExistsWrapper)
            .`when`(identityRegistry)!!
            .identityExists(metadiumDID.ein)

        val identityRemoteFunctionCall = mock(RemoteFunctionCall::class.java)
        val identity: Tuple4<String, List<String>, List<String>, List<String>> = Tuple4(
            "0x0c65a336fc97d4cf830baeb739153f312cbefcc9",
            associatedAddresses,
            listOf("0x298fde31b830f43b664e32d84180462802c4ec01", "0x85d9d6df80356ac3893c63dba54560afb10fef78"),
            resolverAddresses
        )

        doReturn(identity)
            .`when`(identityRemoteFunctionCall)
            .send()

        doReturn(identityRemoteFunctionCall)
            .`when`(identityRegistry)!!
            .getIdentity(metadiumDID.ein)

        keyManagerService = KeyManagerService(identityRegistry!!, mockPublicKeyResolvers, mockServiceKeyResolvers)

        // When
        val result = keyManagerService!!.createKeyManager(metadiumDID)

        // Then
        val expectedPublicKeyResolverAddresses = mockPublicKeyResolvers.map { it.contractAddress }.toSet().intersect(resolverAddresses).toList()
        val expectedServiceKeyResolverAddresses = mockServiceKeyResolvers.map { it.contractAddress }.toSet().intersect(resolverAddresses).toList()

        assertThat(result).isNotNull
        result.map {
            assertThat(it).isNotNull
            assertThat(it.ownerDID.toString()).isEqualTo(metadiumDID.toString())
            assertThat(it.associatedAddresses).containsExactlyInAnyOrderElementsOf(associatedAddresses)
            assertThat(it.publicKeyResolvers.map { k -> k.contractAddress }).containsExactlyInAnyOrderElementsOf(expectedPublicKeyResolverAddresses)
            assertThat(it.serviceKeyResolvers.map { k -> k.contractAddress }).containsExactlyInAnyOrderElementsOf(expectedServiceKeyResolverAddresses)
        }
    }

    @Test
    @DisplayName("Identity 가 존재하지 않을 경우 NotFoundIdentityException 을 반환해야 한다")
    fun should_return_NotFoundIdentityException_when_identity_not_found() {

        // Given
        val metadiumDID = MetadiumDID("did:meta:000000000000000000000000000000000000000000000000000000000000112b")

        val identityExistsWrapper = mock(RemoteFunctionCall::class.java)
        doReturn(false)
            .`when`(identityExistsWrapper)
            .send()

        doReturn(identityExistsWrapper)
            .`when`(identityRegistry)!!
            .identityExists(metadiumDID.ein)

        keyManagerService = KeyManagerService(identityRegistry!!, publicKeyResolvers!!, serviceKeyResolvers!!)

        // When
        val result = keyManagerService!!.createKeyManager(metadiumDID)

        // Then
        assertThat(result).isNotNull
        result.mapLeft {
            assertThat(it).isNotNull
            assertThat(it).isInstanceOf(NotFoundIdentityException::class.java)
            assertThat(it.message).isEqualTo("Not found")
        }
    }

    @Test
    @DisplayName("Identity 가 삭제되었을 경우 NotFoundIdentityException 을 반환해야 한다")
    fun should_return_NotFoundIdentityException_when_identity_is_deleted() {

        // Given
        val metadiumDID = MetadiumDID("did:meta:000000000000000000000000000000000000000000000000000000000000112b")

        val identityExistsWrapper = mock(RemoteFunctionCall::class.java)
        doReturn(true)
            .`when`(identityExistsWrapper)
            .send()

        doReturn(identityExistsWrapper)
            .`when`(identityRegistry)!!
            .identityExists(metadiumDID.ein)

        val identityRemoteFunctionCall = mock(RemoteFunctionCall::class.java)
        val identity: Tuple4<String, List<String>, List<String>, List<String>> = Tuple4(
            "0x0c65a336fc97d4cf830baeb739153f312cbefcc9",
            emptyList(),
            listOf("0x298fde31b830f43b664e32d84180462802c4ec01", "0x85d9d6df80356ac3893c63dba54560afb10fef78"),
            listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331"))

        doReturn(identity)
            .`when`(identityRemoteFunctionCall)
            .send()

        doReturn(identityRemoteFunctionCall)
            .`when`(identityRegistry)!!
            .getIdentity(metadiumDID.ein)

        keyManagerService = KeyManagerService(identityRegistry!!, publicKeyResolvers!!, serviceKeyResolvers!!)

        // When
        val result = keyManagerService!!.createKeyManager(metadiumDID)

        // Then
        assertThat(result).isNotNull
        result.mapLeft {
            assertThat(it).isNotNull
            assertThat(it).isInstanceOf(NotFoundIdentityException::class.java)
            assertThat(it.message).isEqualTo("Deleted meta id")
        }
    }

    companion object {

        @JvmStatic
        fun should_return_keyManager_argumentsProvider(): Stream<Arguments>? {
            return Stream.of(
                Arguments.of(
                    // Associated addresses
                    listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
                    // Resolver addresses
                    listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331"),
                    // Mock public key resolvers
                    givenMockPublicKeyResolvers(listOf("0xd9f39ab902f835400cfb424529bb0423d7342331")),
                    // Mock service key resolvers
                    givenMockServiceKeyResolvers(listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"))
                ),
                Arguments.of(
                    // Associated addresses
                    listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
                    // Resolver addresses
                    listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331", "0xc8f39ab902f835400cfb424529bb0423d7342331"),
                    // Mock public key resolvers
                    givenMockPublicKeyResolvers(listOf("0xd9f39ab902f835400cfb424529bb0423d7342331", "0xddddddd902f835400cfb424529bb0423d7342331")),
                    // Mock service key resolvers
                    givenMockServiceKeyResolvers(listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"))
                ),
                Arguments.of(
                    // Associated addresses
                    listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
                    // Resolver addresses
                    listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331"),
                    // Mock public key resolvers
                    givenMockPublicKeyResolvers(listOf("0xd9f39ab902f835400cfb424529bb0423d7342331")),
                    // Mock service key resolvers
                    givenMockServiceKeyResolvers(listOf())
                ),
                Arguments.of(
                    // Associated addresses
                    listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
                    // Resolver addresses
                    listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331"),
                    // Mock public key resolvers
                    givenMockPublicKeyResolvers(listOf()),
                    // Mock service key resolvers
                    givenMockServiceKeyResolvers(listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"))
                ),
                Arguments.of(
                    // Associated addresses
                    listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
                    // Resolver addresses
                    listOf("0xed4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xf9f39ab902f835400cfb424529bb0423d7342331"),
                    // Mock public key resolvers
                    givenMockPublicKeyResolvers(listOf("0xd9f39ab902f835400cfb424529bb0423d7342331")),
                    // Mock service key resolvers
                    givenMockServiceKeyResolvers(listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"))
                )
            )
        }

        /**
         * Mock public key resolver 리스트 생성 함수
         */
        private fun givenMockPublicKeyResolvers(contractAddresses: List<String>): List<PublicKeyResolver> {
            return contractAddresses.parallelStream().map {
                val mockPublicKeyResolver = mock(PublicKeyResolver::class.java)
                doReturn(it)
                    .`when`(mockPublicKeyResolver)
                    .contractAddress
                mockPublicKeyResolver
            }.collect(Collectors.toList())
        }

        /**
         * Mock service key resolver 리스트 생성 함수
         */
        private fun givenMockServiceKeyResolvers(contractAddresses: List<String>): List<ServiceKeyResolver> {
            return contractAddresses.parallelStream().map {
                val mockServiceKeyResolver = mock(ServiceKeyResolver::class.java)
                doReturn(it)
                    .`when`(mockServiceKeyResolver)
                    .contractAddress
                mockServiceKeyResolver
            }.collect(Collectors.toList())
        }
    }
}
