package io.omnipede.metadium.did.resolver.infra.contract

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MetadiumContractConfigTest {

    private lateinit var metadiumContractConfig: MetadiumContractConfig

    @BeforeEach
    fun setup() {
        metadiumContractConfig = MetadiumContractConfig()
        metadiumContractConfig.httpProvider = "https://api.metadium.com/prod"
        metadiumContractConfig.identityRegistryAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
        metadiumContractConfig.publicKeyResolverAddressList = listOf("0xd9f39ab902f835400cfb424529bb0423d7342331");
        metadiumContractConfig.serviceKeyResolverAddressList = listOf("0x5d4b8c6c6abecf9b5277747fa15980b964c40ce3")
    }

    @Test
    fun web3j_bean_생성_테스트() {

        // Given
        val web3j = metadiumContractConfig.web3j()

        // Then
        assertThat(web3j).isNotNull
    }

    @Test
    fun dummy_credential_bean_생성_테스트() {

        // Given
        val credentials = metadiumContractConfig.credentials()

        // Then
        assertThat(credentials).isNotNull
    }

    @Test
    fun identityRegistry_bean_생성_테스트() {

        // Given
        val web3j = metadiumContractConfig.web3j()
        val credentials = metadiumContractConfig.credentials()
        val identityRegistry = metadiumContractConfig.identityRegistry(web3j, credentials)

        // Then
        assertThat(identityRegistry).isNotNull
    }

    @Test
    fun publicKeyResolvers_bean_생성_테스트() {

        // Given
        val web3j = metadiumContractConfig.web3j()
        val credentials = metadiumContractConfig.credentials()
        val publicKeyResolvers: List<PublicKeyResolver> = metadiumContractConfig.publicKeyResolvers(web3j, credentials)

        // Then
        assertThat(publicKeyResolvers).isNotNull
        assertThat(publicKeyResolvers).hasSize(1)
    }

    @Test
    fun serviceKeyResolvers_bean_생성_테스트() {

        // Given
        val web3j = metadiumContractConfig.web3j()
        val credentials = metadiumContractConfig.credentials()
        val serviceKeyResolvers: List<ServiceKeyResolver> = metadiumContractConfig.serviceKeyResolvers(web3j, credentials)

        // Then
        assertThat(serviceKeyResolvers).isNotNull
        assertThat(serviceKeyResolvers).hasSize(1)
    }
}