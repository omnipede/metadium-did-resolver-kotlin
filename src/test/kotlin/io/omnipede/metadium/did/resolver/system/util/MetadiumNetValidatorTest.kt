package io.omnipede.metadium.did.resolver.system.util

import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import javax.validation.Validation
import javax.validation.Validator

internal class MetadiumNetValidatorTest {

    var validator: Validator? = null

    // @MetadiumNet annotation 을 적용한 클래스
    class Usage {
        @MetadiumNet
        var network: String? = null
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
                "mainnet", "testnet"
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
    fun validation_test(network: String) {
        // Given
        val usage = Usage()
        usage.network = network

        // When
        val violations = validator?.validate(usage)

        // Then
        AssertionsForInterfaceTypes.assertThat(violations).isNotNull
        AssertionsForInterfaceTypes.assertThat(violations).isEmpty()
    }

    @ParameterizedTest(name = "Constraint 에러 케이스: {index}")
    @MethodSource("wrongMethodSource")
    fun wrong_validation_test(network: String?) {
        // Given
        val usage = Usage()
        usage.network = network

        // When
        val violations = validator?.validate(usage)

        // Then
        AssertionsForInterfaceTypes.assertThat(violations).isNotNull
        AssertionsForInterfaceTypes.assertThat(violations).hasSize(1)
    }
}