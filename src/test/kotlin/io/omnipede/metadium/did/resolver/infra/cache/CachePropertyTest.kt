package io.omnipede.metadium.did.resolver.infra.cache

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class CachePropertyTest {

    @Test
    @DisplayName("객체 생성 테스트")
    fun constructor_test() {

        // Given
        val duration = 60 * 10L
        val maximumSize = 10000L

        // When
        val property = CacheProperty()
        property.duration = duration
        property.maximumSize = maximumSize

        // Then
        assertThat(property.duration).isEqualTo(duration)
        assertThat(property.maximumSize).isEqualTo(maximumSize)
    }
}
