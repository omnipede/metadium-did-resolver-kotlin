package io.omnipede.metadium.did.resolver.system.config

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class FilterConfigTest {

    @Test
    @DisplayName("Filter 생성 테스트")
    fun constructor_test() {

        // Given
        val filterConfig = FilterConfig()

        // WHen
        val configurer = filterConfig.accessLogFilterConfigurer()
        val filter = filterConfig.accessLogFilter(configurer)

        // Then
        assertThat(filter).isNotNull
    }
}
