package io.omnipede.metadium.did.resolver.system.config

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class BeanConfigTest {

    private var beanConfig: BeanConfig? = null

    @BeforeEach
    fun setup() {
        beanConfig = BeanConfig()
    }

    @Test
    @DisplayName("Undertow bean 테스트")
    fun undertow_factory_should_be_generated() {

        // Given

        // When
        val undertowServletFactory = beanConfig?.embeddedServletContainerFactory()

        // Then
        assertThat(undertowServletFactory).isNotNull
    }
}
