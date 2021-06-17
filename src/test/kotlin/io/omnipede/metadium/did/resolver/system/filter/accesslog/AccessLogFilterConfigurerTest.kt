package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


internal class AccessLogFilterConfigurerTest {

    @Test
    @DisplayName("객체 생성 테스트")
    fun constructor_test() {

        // Given
        val maxContentLength = 2048
        val enableContentLogging = false
        val configurer = AccessLogFilterConfigurer(emptyList(), maxContentLength, enableContentLogging)
        val configurer2 = AccessLogFilterConfigurer(emptyList(), maxContentLength)
        val configurer3 = AccessLogFilterConfigurer(emptyList())
        val configurer4 = AccessLogFilterConfigurer()

        // When

        // Then
        assertThat(configurer.maxContentLength).isEqualTo(maxContentLength)
        assertThat(configurer.enableContentLogging).isEqualTo(enableContentLogging)

        assertThat(configurer2.maxContentLength).isEqualTo(maxContentLength)
        assertThat(configurer2.enableContentLogging).isEqualTo(false)

        assertThat(configurer3.maxContentLength).isEqualTo(1024)
        assertThat(configurer3.enableContentLogging).isEqualTo(false)

        assertThat(configurer4.maxContentLength).isEqualTo(1024)
        assertThat(configurer4.enableContentLogging).isEqualTo(false)
    }

    @Test
    @DisplayName("maxContentLength 는 10GB 를 넘어서는 안된다")
    fun should_throw_IllegalArgumentException_when_maxContentLength_is_too_large() {

        // Given
        val maxContentLength = 1024 * 1024 * 1024 + 1

        // When
        val throwable = catchThrowable {
            AccessLogFilterConfigurer(
                whiteList = emptyList(), maxContentLength = maxContentLength, enableContentLogging = false
            )
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(throwable.message).isEqualTo("maxContentLength should not be larger than ${1024 * 1024 * 1024}")
    }

    @ParameterizedTest(name = "isWhiteListed() 메소드 테스트: {index}")
    @ValueSource(strings = [
        "/api/v1/uri", "/api/v1/uri/", "/api/v1/uri/endpoint", "/favicon.ico"
    ])
    fun isWhiteListed_should_return_true_when_uri_is_whitelisted(uri: String) {

        // Given
        val whiteList = listOf("/api/v1/uri", "/favicon.ico")
        val configurer = AccessLogFilterConfigurer(
            whiteList, maxContentLength = 128, enableContentLogging = true
        )

        // When
        val result = configurer.isWhiteListed(uri)

        // Then
        assertThat(result).isTrue
    }

    @ParameterizedTest(name = "isWhiteListed() 메소드 false 반환 테스트: {index}")
    @ValueSource(strings = [
        "/", "/api", "/api/v1/ur", "/api/v2/uri/", "/foobar/foobar"
    ])
    fun isWhiteListed_should_return_false_when_uri_is_not_whiteListed(uri: String) {

        // Given
        val whiteList = listOf("/api/v1/uri", "/favicon.ico")
        val configurer = AccessLogFilterConfigurer(
            whiteList, maxContentLength = 128, enableContentLogging = true
        )

        // When
        val result = configurer.isWhiteListed(uri)

        // Then
        assertThat(result).isFalse
    }
}
