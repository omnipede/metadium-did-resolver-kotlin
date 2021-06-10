package io.omnipede.metadium.did.resolver.domain

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.lang.IllegalArgumentException

internal class MetadiumDIDTest {

    @ParameterizedTest(name = "Mainnet DID 확인 {index}")
    @ValueSource(strings = [
        "did:meta:mainnet:000000000000000000000000000000000000000000000000000000000000112b",
        "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
    ])
    fun mainnet_did_test(did: String) {
        // Given

        // When
        val metaDID = MetadiumDID(did)

        // Then
        assertThat(metaDID.net).isEqualTo("mainnet")
        assertThat(metaDID.ein).isEqualTo("000000000000000000000000000000000000000000000000000000000000112b".toBigInteger(16))
        assertThat(metaDID.toString()).isEqualTo("did:meta:000000000000000000000000000000000000000000000000000000000000112b")
    }

    @ParameterizedTest(name = "Testnet DID 확인")
    @ValueSource(strings = [
        "did:meta:testnet:000000000000000000000000000000000000000000000000000000000000112b"
    ])
    fun testnet_did_test(did: String) {
        // Given

        // When
        val metaDID = MetadiumDID(did)

        // Then
        assertThat(metaDID.net).isEqualTo("testnet")
        assertThat(metaDID.ein).isEqualTo("000000000000000000000000000000000000000000000000000000000000112b".toBigInteger(16))
        assertThat(metaDID.toString()).isEqualTo("did:meta:testnet:000000000000000000000000000000000000000000000000000000000000112b")
    }

    @Test
    @DisplayName("틀린 DID 형식일 경우 IllegalArgumentException 이 발생해야 함")
    fun should_throw_IllegalArgumentException_with_invalid_did_format() {
        // Given
        val wrongDID = "did:meta:testnet:000000000000000000000000000000000000000000000000000000000000112b:"

        // When
        val throwable: Throwable? = catchThrowable {
            MetadiumDID(wrongDID)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(throwable).hasMessageContaining("Invalid DID format: $wrongDID")
    }
}
