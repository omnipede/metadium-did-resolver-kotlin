package io.omnipede.metadium.did.resolver.infra.env

import io.omnipede.metadium.did.resolver.system.config.MetadiumConfigProperty
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class PropertyServiceTest {

    @Test
    @DisplayName("동일 네트워크일 경우 true 반환")
    fun ifSameNetwork() {

        // Given
        val metadiumConfigProperty = MetadiumConfigProperty()
        metadiumConfigProperty.network = "mainnet"

        // When
        val propertyService = PropertyService(metadiumConfigProperty)

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

        // When
        val propertyService = PropertyService(metadiumConfigProperty)

        // Then
        val result = propertyService.isSameNetwork("testnet")
        assertThat(result).isFalse
    }
}