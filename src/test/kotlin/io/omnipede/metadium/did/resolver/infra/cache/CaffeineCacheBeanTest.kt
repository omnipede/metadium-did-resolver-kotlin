package io.omnipede.metadium.did.resolver.infra.cache

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class CaffeineCacheBeanTest {

    @Test
    @DisplayName("객체 생성 테스트")
    fun constructor_test() {

        // Given
        val property = CacheProperty()
        property.duration = 60 * 1000L
        property.maximumSize = 1000L

        // When
        val caffeineCacheBean = CaffeineCacheBean(property)
        val caffeineCache = caffeineCacheBean.cache()

        // Then
        assertThat(caffeineCacheBean).isNotNull
        assertThat(caffeineCache).isNotNull
    }
}
