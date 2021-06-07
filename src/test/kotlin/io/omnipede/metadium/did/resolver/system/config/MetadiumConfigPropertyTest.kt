package io.omnipede.metadium.did.resolver.system.config

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class MetadiumConfigPropertyTest {

    @ParameterizedTest(name = "객체 생성 테스트: {0}")
    @ValueSource(strings = [
        "mainnet", "testnet"
    ])
    fun constructor_test(network: String) {
        // Given
        val metadiumConfigProperty = MetadiumConfigProperty()
        metadiumConfigProperty.network = network
        metadiumConfigProperty.httpProvider = "https://api.metadium.com/prod"
        metadiumConfigProperty.identityRegistryAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
        metadiumConfigProperty.publicKeyResolverAddressList = listOf("0xd9f39ab902f835400cfb424529bb0423d7342331")
        metadiumConfigProperty.serviceKeyResolverAddressList = listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3")

        // Then
        assertThat(metadiumConfigProperty.network).isEqualTo(network)
        assertThat(metadiumConfigProperty.httpProvider).isEqualTo("https://api.metadium.com/prod")
        assertThat(metadiumConfigProperty.identityRegistryAddress).isEqualTo("0x42bbff659772231bb63c7c175a1021e080a4cf9d")
        assertThat(metadiumConfigProperty.publicKeyResolverAddressList).hasSameElementsAs(
            listOf("0xd9f39ab902f835400cfb424529bb0423d7342331"))
        assertThat(metadiumConfigProperty.serviceKeyResolverAddressList).hasSameElementsAs(
            listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3"))
    }
}