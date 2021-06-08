package io.omnipede.metadium.did.resolver.system.config

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class ResolverPropertyTest {

    @Test
    @DisplayName("반환값이 정상이어야 한다")
    fun property_should_be_created() {

        // Given
        val resolverProperty = ResolverProperty()
        resolverProperty.driverId = "FoobarId"

        // When

        // Then
        assertThat(resolverProperty).isNotNull
        assertThat(resolverProperty.driverId).isEqualTo("FoobarId")
    }
}