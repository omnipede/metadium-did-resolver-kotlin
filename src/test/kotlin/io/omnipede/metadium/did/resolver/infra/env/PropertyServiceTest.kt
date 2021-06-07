package io.omnipede.metadium.did.resolver.infra.env

import io.omnipede.metadium.did.resolver.domain.PublicKey
import io.omnipede.metadium.did.resolver.system.config.IdentityHubProperty
import io.omnipede.metadium.did.resolver.system.config.MetadiumConfigProperty
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PropertyServiceTest {

    @Nested
    @DisplayName("isSameNetwork()")
    inner class IsSameNetworkTest {

        @Test
        @DisplayName("동일 네트워크일 경우 true 반환")
        fun ifSameNetwork() {

            // Given
            val metadiumConfigProperty = MetadiumConfigProperty()
            metadiumConfigProperty.network = "mainnet"
            val identityHubProperty = IdentityHubProperty()
            identityHubProperty.id = "did:meta:0000000000000000000000000000000000000000000000000000000000000527"
            identityHubProperty.url = "https://datahub.metadium.com"

            // When
            val propertyService = PropertyService(metadiumConfigProperty, identityHubProperty)

            // Then
            val result = propertyService.isSameNetwork("mainnet")
            assertThat(result).isTrue
        }

        @Test
        @DisplayName("다른 네트워크일 경우 false 반환")
        fun ifDifferentNetwork() {

            // Given
            val metadiumConfigProperty = MetadiumConfigProperty()
            metadiumConfigProperty.network = "mainnet"
            val identityHubProperty = IdentityHubProperty()
            identityHubProperty.id = "did:meta:0000000000000000000000000000000000000000000000000000000000000527"
            identityHubProperty.url = "https://datahub.metadium.com"

            // When
            val propertyService = PropertyService(metadiumConfigProperty, identityHubProperty)

            // Then
            val result = propertyService.isSameNetwork("testnet")
            assertThat(result).isFalse
        }
    }

    @Nested
    @DisplayName("createService()")
    inner class CreateService {

        @Test
        @DisplayName("반환값이 정상이어야 한다")
        fun associatedService_should_be_created() {

            // Given
            val metadiumConfigProperty = MetadiumConfigProperty()
            val identityHubProperty = IdentityHubProperty()
            identityHubProperty.id = "did:meta:0000000000000000000000000000000000000000000000000000000000000527"
            identityHubProperty.url = "https://datahub.metadium.com"

            val publicKey = PublicKey(
                did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b",
                keyId = "Testing",
                address = "0x0C65a336fc97d4cf830baeb739153f312cbefcc9"
            )

            // When
            val propertyService = PropertyService(metadiumConfigProperty, identityHubProperty)
            val result = propertyService.createService(publicKey)

            // Then
            assertThat(result).isNotNull
            assertThat(result.id).isEqualTo(identityHubProperty.id)
            assertThat(result.serviceEndpoint).isEqualTo(identityHubProperty.url)
            assertThat(result.publicKey).isEqualTo(publicKey.id)
        }
    }
}