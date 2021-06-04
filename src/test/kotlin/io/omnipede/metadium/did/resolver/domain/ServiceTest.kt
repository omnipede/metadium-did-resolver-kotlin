package io.omnipede.metadium.did.resolver.domain

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class ServiceTest {

    @Test
    @DisplayName("Service 객체 생성 테스트")
    fun Service_객체_생성_테스트() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val publicKey = PublicKey(
            did=did, keyId="testing", address="0x0c65a336fc97d4cf830baeb739153f312cbefcc9"
        )
        val url = "https://datahub.metadium.com"

        // When
        val service = Service(did=did, publicKey=publicKey, url=url)

        // Then
        assertThat(service).isNotNull
        assertThat(service.id).isEqualTo(did)
        assertThat(service.publicKey).isEqualTo(publicKey.id)
        assertThat(service.type).isEqualTo("identityHub")
        assertThat(service.serviceEndpoint).isEqualTo(url)
    }

    @Test
    @DisplayName("DID 형식이 이상할 때 IllegalArgumentException 이 발생하는지 확인")
    fun did_형식이_이상할_때_IllegalArgumentException_발생하는지_확인() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112C"
        val publicKey = PublicKey(
            did="did:meta:000000000000000000000000000000000000000000000000000000000000112b",
            keyId="testing",
            address="0x0c65a336fc97d4cf830baeb739153f312cbefcc9"
        )
        val url = "https://datahub.metadium.com"

        // When
        val throwable: Throwable? = catchThrowable {
            Service(did=did, publicKey=publicKey, url=url)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(throwable).hasMessageContaining("Invalid DID format: $did")
    }

    @Test
    @DisplayName("URL 형식이 이상할 때 IllegalArgumentException 이 발생하는지 확인")
    fun url_형식이_이상할_때_IllegalArgumentException_발생하는지_확인() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val publicKey = PublicKey(
            did=did,
            keyId="testing",
            address="0x0c65a336fc97d4cf830baeb739153f312cbefcc9"
        )
        val url = "ftp://datahub.metadium.com"

        // When
        val throwable: Throwable? = catchThrowable {
            Service(did=did, publicKey=publicKey, url=url)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(throwable).hasMessageContaining("Invalid URL format: $url")
    }
}