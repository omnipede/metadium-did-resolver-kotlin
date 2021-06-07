package io.omnipede.metadium.did.resolver.system.util

import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import javax.validation.Validation
import javax.validation.Validator

internal class MetadiumDIDValidatorTest {

    var validator: Validator? = null

    // @MetadiumDID annotation 을 적용한 클래스
    class Usage {
        @MetadiumDID
        var did: String? = null
    }

    @BeforeEach
    fun setup() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    /**
     * MethodSource definition of below parameterized test
     */
    companion object {

        @JvmStatic
        fun correctMethodSource(): Stream<String> {
            return Stream.of(
                "did:meta:0000000000000000000000000000000000000000000000000000000000000527",
                "did:meta:testnet:0000000000000000000000000000000000000000000000000000000000000527"
            )
        }

        @JvmStatic
        fun wrongMethodSource(): Stream<String> {
            return Stream.of(
                "wrong", null
            )
        }
    }

    @ParameterizedTest(name = "Constraint 정상 케이스: {0}")
    @MethodSource("correctMethodSource")
    fun validation_test(did: String) {
        // Given
        val usage = Usage()
        usage.did = did

        // When
        val violations = validator?.validate(usage)

        // Then
        AssertionsForInterfaceTypes.assertThat(violations).isNotNull
        AssertionsForInterfaceTypes.assertThat(violations).isEmpty()
    }

    @ParameterizedTest(name = "Constraint 에러 케이스: {index}")
    @MethodSource("wrongMethodSource")
    fun wrong_validation_test(did: String?) {
        // Given
        val usage = Usage()
        usage.did = did

        // When
        val violations = validator?.validate(usage)

        // Then
        AssertionsForInterfaceTypes.assertThat(violations).isNotNull
        AssertionsForInterfaceTypes.assertThat(violations).hasSize(1)
    }
}