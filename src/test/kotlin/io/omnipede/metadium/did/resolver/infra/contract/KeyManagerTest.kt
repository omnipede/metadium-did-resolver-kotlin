package io.omnipede.metadium.did.resolver.infra.contract

import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.web3j.protocol.core.RemoteFunctionCall
import java.util.*
import javax.xml.bind.DatatypeConverter

internal class KeyManagerTest {

    private val ownerDID: MetadiumDID = MetadiumDID("did:meta:000000000000000000000000000000000000000000000000000000000000112b")
    private val commonPublicKeyHex = "49f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83"
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    @DisplayName("객체 생성 테스트")
    fun constructor_test() {

        // Given
        val associatedAddresses = listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9", "0x1d65a336fc97d4cf830baeb739153f312cbefcc9")
        val mockPublicKeyResolvers = listOf(
            givenMockPublicKeyResolver(associatedAddresses[0])
        )
        val serviceKeyAddresses = listOf("0x1265a312fc97d12f830bae1239153f31212efcc9", "0x4865a312f317d12f830bae1239153f31222efec4")
        val serviceSymbol = UUID.randomUUID().toString()
        val mockServiceKeyResolvers = listOf(
            givenMockServiceKeyResolver(serviceKeyAddresses, serviceSymbol),
        )

        // When
        val keyManager = KeyManager(ownerDID, associatedAddresses, mockPublicKeyResolvers, mockServiceKeyResolvers)

        // Then
        assertThat(keyManager).isNotNull
        assertThat(keyManager.ownerDID).isEqualTo(ownerDID)
        assertThat(keyManager.associatedAddresses).isEqualTo(associatedAddresses)
        assertThat(keyManager.publicKeyResolvers).isEqualTo(mockPublicKeyResolvers)
        assertThat(keyManager.serviceKeyResolvers).isEqualTo(mockServiceKeyResolvers)
    }

    @Test
    @DisplayName("Public key 조회 테스트")
    fun should_return_public_key_list() {

        // Given
        val associatedAddresses = listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9", "0x1d65a336fc97d4cf830baeb739153f312cbefcc9")
        val mockPublicKeyResolvers = listOf(
            givenMockPublicKeyResolver(associatedAddresses[0]),
            givenMockPublicKeyResolver("0x7777a336fc97d4cf8307777739153f312cbef777")
        )
        val serviceKeyAddresses = listOf("0x1265a312fc97d12f830bae1239153f31212efcc9", "0x4865a312f317d12f830bae1239153f31222efec4")
        val serviceSymbol = UUID.randomUUID().toString()
        val mockServiceKeyResolvers = listOf(
            givenMockServiceKeyResolver(serviceKeyAddresses, serviceSymbol),
        )

        val keyManager = KeyManager(ownerDID, associatedAddresses, mockPublicKeyResolvers, mockServiceKeyResolvers)

        // When
        val publicKeys = keyManager.findPublicKeys()

        // Then
        assertThat(publicKeys).isNotNull
        val expectedPublicKeys = associatedAddresses.map {
            val publicKey = PublicKey(did = ownerDID.toString(), "MetaManagementKey", it)
            if (it == "0x0c65a336fc97d4cf830baeb739153f312cbefcc9")
                publicKey.publicKeyHex = commonPublicKeyHex
            publicKey
        }
        assertThat(publicKeys).usingRecursiveComparison().isEqualTo(expectedPublicKeys)
    }

    @Test
    @DisplayName("Service key 조회 테스트")
    fun should_return_service_key_list() {

        // Given
        val associatedAddresses = listOf("0x0c65a336fc97d4cf830baeb739153f312cbefcc9", "0x1d65a336fc97d4cf830baeb739153f312cbefcc9")
        val mockPublicKeyResolvers = listOf(givenMockPublicKeyResolver(associatedAddresses[0]))

        val serviceKeyAddresses1 = listOf("0x1265a312fc97d12f830bae1239153f31212efcc9", "0x4865a312f317d12f830bae1239153f31222efec4")
        val serviceSymbol1 = UUID.randomUUID().toString()

        val serviceKeyAddresses2 = listOf("0x4234a3112347d12f830bae1239153f31212efcc9", "0x32567812f317d12f830bae1239153908722ef2c4")
        val serviceSymbol2 = UUID.randomUUID().toString()

        val mockServiceKeyResolvers = listOf(
            givenMockServiceKeyResolver(serviceKeyAddresses1, serviceSymbol1),
            givenMockServiceKeyResolver(serviceKeyAddresses2, serviceSymbol2)
        )

        val keyManager = KeyManager(ownerDID, associatedAddresses, mockPublicKeyResolvers, mockServiceKeyResolvers)

        // When
        val serviceKeys = keyManager.findServiceKeys()

        // Then
        assertThat(serviceKeys).isNotNull
        val expectedServiceKeys = listOf(
            serviceKeyAddresses1.map {
                PublicKey(did = ownerDID.toString(), keyId = serviceSymbol1, it)
            },
            serviceKeyAddresses2.map {
                PublicKey(did = ownerDID.toString(), keyId = serviceSymbol2, it)
            }
        ).flatten()
        assertThat(serviceKeys).usingRecursiveComparison().isEqualTo(expectedServiceKeys)
    }

    private fun givenMockPublicKeyResolver(associatedAddress: String): PublicKeyResolver {
        val mockPublicKeyResolver = mock(PublicKeyResolver::class.java)

        // Mock getPublicKey()
        val getPublicKeyRemoteFunctionCall1 = mock(RemoteFunctionCall::class.java)
        doReturn(
            DatatypeConverter.parseHexBinary(commonPublicKeyHex)
        ).`when`(getPublicKeyRemoteFunctionCall1)
            .send()

        // Mock getPublicKey()
        val getPublicKeyRemoteFunctionCall2 = mock(RemoteFunctionCall::class.java)
        doReturn(
            DatatypeConverter.parseHexBinary("")
        )
            .`when`(getPublicKeyRemoteFunctionCall2)
            .send()

        doAnswer {
            val argument = it.arguments[0]
            // Parameter 로 associatedAddress 가 세팅된 경우에만 publicKeyHex 를 반환한다
            val returnValue = if (argument == associatedAddress) {
                getPublicKeyRemoteFunctionCall1
            } else {
                getPublicKeyRemoteFunctionCall2
            }
            returnValue
        }.`when`(mockPublicKeyResolver)
            .getPublicKey(any(String::class.java))

        return mockPublicKeyResolver
    }

    private fun givenMockServiceKeyResolver(serviceKeyAddresses: List<String>, serviceKey: String): ServiceKeyResolver {
        val mockServiceKeyResolver = mock(ServiceKeyResolver::class.java)

        // Mock getKeys()
        val getKeysRemoteFunctionCall = mock(RemoteFunctionCall::class.java)
        doReturn(serviceKeyAddresses)
            .`when`(getKeysRemoteFunctionCall)
            .send()
        doReturn(getKeysRemoteFunctionCall)
            .`when`(mockServiceKeyResolver)
            .getKeys(ownerDID.ein)

        // Mock getSymbol()
        val getSymbolRemoteFunctionCall = mock(RemoteFunctionCall::class.java)
        doReturn(serviceKey)
            .`when`(getSymbolRemoteFunctionCall)
            .send()
        doReturn(getSymbolRemoteFunctionCall)
            .`when`(mockServiceKeyResolver)
            .getSymbol(any(String::class.java))

        return mockServiceKeyResolver
    }
}
