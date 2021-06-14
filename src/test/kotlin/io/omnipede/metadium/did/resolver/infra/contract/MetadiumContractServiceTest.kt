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
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.web3j.protocol.core.RemoteFunctionCall
import org.web3j.tuples.generated.Tuple4
import java.util.*
import java.util.stream.Stream
import javax.xml.bind.DatatypeConverter

internal class MetadiumContractServiceTest {

    private var metadiumContractService: MetadiumContractService? = null
    private var identityRegistry: IdentityRegistry? = null
    private var publicKeyResolvers: MutableList<PublicKeyResolver>? = null
    private var serviceKeyResolvers: MutableList<ServiceKeyResolver>? = null

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @BeforeEach
    fun setup() {

        identityRegistry = mock(IdentityRegistry::class.java)
        publicKeyResolvers = mutableListOf()
        serviceKeyResolvers = mutableListOf()
        metadiumContractService = MetadiumContractService(identityRegistry!!, publicKeyResolvers!!, serviceKeyResolvers!!)
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

        // When
        val result = metadiumContractService!!.findPublicKeyList(metadiumDID)

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

        val identityWrapper = mock(RemoteFunctionCall::class.java)
        val identity: Tuple4<String, List<String>, List<String>, List<String>> = Tuple4(
            "0x0c65a336fc97d4cf830baeb739153f312cbefcc9",
            emptyList(),
            listOf("0x298fde31b830f43b664e32d84180462802c4ec01", "0x85d9d6df80356ac3893c63dba54560afb10fef78"),
            listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331"))

        doReturn(identity)
            .`when`(identityWrapper)
            .send()

        doReturn(identityWrapper)
            .`when`(identityRegistry)!!
            .getIdentity(metadiumDID.ein)

        // When
        val result = metadiumContractService!!.findPublicKeyList(metadiumDID)

        // Then
        assertThat(result).isNotNull
        result.mapLeft {
            assertThat(it).isNotNull
            assertThat(it).isInstanceOf(NotFoundIdentityException::class.java)
            assertThat(it.message).isEqualTo("Deleted meta id")
        }
    }

    @ParameterizedTest(name = "PublicKey 리스트를 올바르게 반환해야 한다: {index}")
    @MethodSource("should_return_public_key_list_argumentsProvider")
    fun should_return_public_key_list(
        metadiumDID: MetadiumDID,
        mockIdentityRegistry: IdentityRegistry,
        mockPublicKeyResolvers: List<PublicKeyResolver>,
        mockServiceKeyResolvers: List<ServiceKeyResolver>
    ) {

        // Given
        metadiumContractService = MetadiumContractService(mockIdentityRegistry, mockPublicKeyResolvers, mockServiceKeyResolvers)

        // When
        val result = metadiumContractService!!.findPublicKeyList(metadiumDID)

        // Then
        assertThat(result.isNotEmpty())

        val identity = mockIdentityRegistry.getIdentity(metadiumDID.ein).send()
        val associatedAddresses = identity.component2()

        result.map {
            assertThat(it).isNotNull
            assertThat(it.publicKeyList).isNotNull
            assertThat(it.serviceKeyList).isNotNull

            it.publicKeyList.forEach { key ->
                assertThat(key.controller).isEqualTo(metadiumDID.toString())
            }

            it.serviceKeyList.forEach { key ->
                assertThat(key.controller).isEqualTo(metadiumDID.toString())
            }
        }
    }

    /**
     * 하단의 코드는 parameterized test 를 위한 argument provider 를 정의하고 있다.
     */
    companion object {

        @JvmStatic
        fun should_return_public_key_list_argumentsProvider(): Stream<Arguments>? {

            val metadiumDID = MetadiumDID("did:meta:000000000000000000000000000000000000000000000000000000000000112b")

            return Stream.of(
                Arguments.of(
                    metadiumDID,
                    givenMockIdentityRegistry(
                        metadiumDID,
                        listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
                        listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331")
                    ),
                    // MockPublicKeyResolver List
                    listOf(
                        givenMockPublicKeyResolver(
                        "0xd9f39ab902f835400cfb424529bb0423d7342331", true
                        )
                    ),
                    // MockServiceKeyResolver List
                    listOf(
                        givenMockServiceKeyResolver(
                            metadiumDID,
                            "0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"
                        )
                    )
                ),
                Arguments.of(
                    metadiumDID,
                    givenMockIdentityRegistry(
                        metadiumDID,
                        listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
                        listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331")
                    ),
                    // MockPublicKeyResolver List
                    listOf(
                        givenMockPublicKeyResolver(
                            "0xd9f39ab902f835400cfb424529bb0423d7342331", false
                        )
                    ),
                    // MockServiceKeyResolver List
                    listOf(
                        givenMockServiceKeyResolver(
                            metadiumDID,
                            "0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"
                        )
                    )
                ),
                Arguments.of(
                    metadiumDID,
                    givenMockIdentityRegistry(
                        metadiumDID,
                        // Associated addresses
                        listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9", "0x1d65a336fc97d4cf830baeb739153f312cbefcc9", "0x2e65a336fc97d4cf830baeb739153f312cbefcc9"),
                        // Resolver addresses
                        listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3", "0xd9f39ab902f835400cfb424529bb0423d7342331", "0xe9f39ab902f835400cfb424529bb0423d7342331")
                    ),
                    // MockPublicKeyResolver List
                    listOf(
                        givenMockPublicKeyResolver(
                            "0xd9f39ab902f835400cfb424529bb0423d7342331", false
                        ),
                        givenMockPublicKeyResolver(
                            "0xe9f39ab902f835400cfb424529bb0423d7342331", true
                        )
                    ),
                    // MockServiceKeyResolver List
                    listOf(
                        givenMockServiceKeyResolver(
                            metadiumDID,
                            "0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"
                        )
                    )
                ),
                Arguments.of(
                    metadiumDID,
                    givenMockIdentityRegistry(
                        metadiumDID,
                        // Associated addresses
                        listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9", "0x1d65a336fc97d4cf830baeb739153f312cbefcc9", "0x2e65a336fc97d4cf830baeb739153f312cbefcc9"),
                        // Resolver addresses
                        emptyList()
                    ),
                    listOf(
                        givenMockPublicKeyResolver(
                            "0xd9f39ab902f835400cfb424529bb0423d7342331", false
                        )
                    ),
                    listOf(
                        givenMockServiceKeyResolver(
                            metadiumDID,
                            "0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"
                        )
                    )
                )
            )
        }

        /**
         * Mock identity registry 를 반환하는 메소드
         */
        private fun givenMockIdentityRegistry(metadiumDID: MetadiumDID, associatedAddresses: List<String>, resolverAddresses: List<String>): IdentityRegistry? {
            val mockIdentityRegistry = mock(IdentityRegistry::class.java)
            val identityExistsWrapper = mock(RemoteFunctionCall::class.java)
            doReturn(true)
                .`when`(identityExistsWrapper)
                .send()

            doReturn(identityExistsWrapper)
                .`when`(mockIdentityRegistry)
                .identityExists(metadiumDID.ein)

            val identityWrapper = mock(RemoteFunctionCall::class.java)
            val identity: Tuple4<String, List<String>, List<String>, List<String>> = Tuple4(
                // Fixed. Not important
                "0x0c65a336fc97d4cf830baeb739153f312cbefcc9",
                associatedAddresses,
                // Fixed. Not important
                listOf("0x298fde31b830f43b664e32d84180462802c4ec01", "0x85d9d6df80356ac3893c63dba54560afb10fef78"),
                resolverAddresses)

            doReturn(identity)
                .`when`(identityWrapper)
                .send()

            doReturn(identityWrapper)
                .`when`(mockIdentityRegistry)
                .getIdentity(metadiumDID.ein)

            return mockIdentityRegistry
        }

        /**
         * Mock privateKeyResolver 를 반환하는 메소드
         */
        private fun givenMockPublicKeyResolver(contractAddress: String, hasPublicKeyHex: Boolean): PublicKeyResolver {
            val mockPublicKeyResolver = mock(PublicKeyResolver::class.java)
            doReturn(contractAddress)
                .`when`(mockPublicKeyResolver)
                .contractAddress

            val publicKeyHexByteArray = if (hasPublicKeyHex) {
                DatatypeConverter.parseHexBinary("49f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83")
            } else {
                DatatypeConverter.parseHexBinary("")
            }

            val getPublicKeyRemoteFunctionCall = mock(RemoteFunctionCall::class.java)
            doReturn(publicKeyHexByteArray)
                .`when`(getPublicKeyRemoteFunctionCall)
                .send()

            doReturn(getPublicKeyRemoteFunctionCall)
                .`when`(mockPublicKeyResolver)
                .getPublicKey(any(String::class.java))

            return mockPublicKeyResolver
        }

        /**
         * Mock serviceKeyResolver 를 반환하는 메소드
         */
        private fun givenMockServiceKeyResolver(metadiumDID: MetadiumDID, contractAddress: String): ServiceKeyResolver {
            val mockServiceKeyResolver = mock(ServiceKeyResolver::class.java)
            doReturn(contractAddress)
                .`when`(mockServiceKeyResolver)
                .contractAddress

            val getKeysRemoteFunctionCall = mock(RemoteFunctionCall::class.java)
            doReturn(listOf("0x69245e218e182e67564bd4387070f6588cf77d33"))
                .`when`(getKeysRemoteFunctionCall)
                .send()
            doReturn(getKeysRemoteFunctionCall)
                .`when`(mockServiceKeyResolver)
                .getKeys(metadiumDID.ein)

            val getSymbolRemoteFunctionCall = mock(RemoteFunctionCall::class.java)
            doReturn(UUID.randomUUID().toString())
                .`when`(getSymbolRemoteFunctionCall)
                .send()

            doReturn(getSymbolRemoteFunctionCall)
                .`when`(mockServiceKeyResolver)
                .getSymbol(any(String::class.java))

            return mockServiceKeyResolver
        }
    }
}
