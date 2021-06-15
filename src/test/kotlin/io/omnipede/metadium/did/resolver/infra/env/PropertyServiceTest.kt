package io.omnipede.metadium.did.resolver.infra.env

import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
import io.omnipede.metadium.did.resolver.system.config.IdentityHubProperty
import io.omnipede.metadium.did.resolver.system.config.MetadiumConfigProperty
import io.omnipede.metadium.did.resolver.system.config.ResolverProperty
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PropertyServiceTest {

    private val metadiumConfigProperty = MetadiumConfigProperty()
    private val identityHubProperty = IdentityHubProperty()
    private val resolverProperty = ResolverProperty()

    private var propertyService: PropertyService? = null

    @BeforeEach
    fun setup() {
        metadiumConfigProperty.network = "mainnet"
        metadiumConfigProperty.identityRegistryAddress = "0x42bbff659772231bb63c7c175a1021e080a4cf9d"
        identityHubProperty.id = "did:meta:0000000000000000000000000000000000000000000000000000000000000527"
        identityHubProperty.url = "https://datahub.metadium.com"
        resolverProperty.driverId = "did-meta"

        propertyService = PropertyService(
            metadiumConfigProperty, identityHubProperty, resolverProperty
        )
    }

    @Nested
    @DisplayName("createService()")
    inner class CreateService {

        @Test
        @DisplayName("반환값이 정상이어야 한다")
        fun associatedService_should_be_created() {

            // Given
            val publicKey = PublicKey(
                did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b",
                keyId = "Testing",
                address = "0x0C65a336fc97d4cf830baeb739153f312cbefcc9"
            )

            // When
            val result = propertyService?.createService(publicKey)

            // Then
            assertThat(result).isNotNull
            assertThat(result?.id).isEqualTo(identityHubProperty.id)
            assertThat(result?.serviceEndpoint).isEqualTo(identityHubProperty.url)
            assertThat(result?.publicKey).isEqualTo(publicKey.id)
        }
    }

    @Nested
    @DisplayName("loadMetaData()")
    inner class LoadMetaDataTest {

        @Test
        @DisplayName("반환값이 정상이어야 한다")
        fun metadata_should_be_created() {

            // Given

            // When
            val metadata = propertyService?.loadMetaData()

            // Then
            assertThat(metadata).isNotNull
            assertThat(metadata?.resolverMetaData?.driverId).isEqualTo(resolverProperty.driverId)
            assertThat(metadata?.resolverMetaData?.driver).isEqualTo("HttpDriver")
            assertThat(metadata?.methodMetaData?.network).isEqualTo(metadiumConfigProperty.network)
            assertThat(metadata?.methodMetaData?.registryAddress).isEqualTo(metadiumConfigProperty.identityRegistryAddress)
        }
    }
}
