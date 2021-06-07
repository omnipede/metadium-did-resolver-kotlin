package io.omnipede.metadium.did.resolver.system.util

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import javax.validation.Validation
import javax.validation.Validator

internal class WebUrlValidatorTest {

    var validator: Validator? = null

    // @WebUrl annotation 을 적용한 클래스
    class Usage {
        @WebUrl
        var webUrl: String? = null
    }

    @BeforeEach
    fun setup() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    @DisplayName("Constraint 정상 케이스")
    fun validation_test() {
        // Given
        val usage = Usage()
        usage.webUrl = "https://mytest.com"

        // When
        val violations = validator?.validate(usage)

        // Then
        assertThat(violations).isNotNull
        assertThat(violations).isEmpty()
    }

    /**
     * MethodSource definition of below parameterized test
     */
    companion object {
        @JvmStatic
        fun methodSource(): Stream< String> {
            return Stream.of(
                "htht://mytest.com", null
            )
        }
    }

    @ParameterizedTest(name = "Constraint 에러 케이스: {index}")
    @MethodSource("methodSource")
    fun wrong_validation_test(webUrl: String?) {
        // Given
        val usage = Usage()
        usage.webUrl = webUrl

        // When
        val violations = validator?.validate(usage)

        // Then
        assertThat(violations).isNotNull
        assertThat(violations).hasSize(1)
    }
}