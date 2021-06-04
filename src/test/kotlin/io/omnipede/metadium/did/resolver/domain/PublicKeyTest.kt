package io.omnipede.metadium.did.resolver.domain

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

internal class PublicKeyTest {

    companion object {
        /**
         * PublicKey 객체 생성 테스트 시 사용할 argument set 을 반환하는 메소드
         * @return Argument (did, address, publicKeyHex) 리스트
         */
        @JvmStatic
        fun argumentProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "did:meta:000000000000000000000000000000000000000000000000000000000000112b",
                    "0x0c65a336fc97d4cf830baeb739153f312cbefcc9", null),
                Arguments.of(
                    "did:meta:000000000000000000000000000000000000000000000000000000000000112b",
                    "0x0c65a336fc97d4cf830baeb739153f312cbefcc9", "0x0c65a336fc97d4cf830baeb739153f312cbefcc9")
            )
        }
    }

    @ParameterizedTest(name = "PublicKey 객체 생성 테스트: {index}")
    @MethodSource("argumentProvider")
    fun publicKey_객체_생성(did: String, address: String, publicKeyHex: String?) {
        // Given
        val keyId = "Testing"

        // When
        val publicKey = if (publicKeyHex == null)
            PublicKey(did=did, keyId=keyId, address=address)
        else PublicKey(did=did, keyId=keyId, address=address, publicKey = publicKeyHex)

        // Then
        assertThat(publicKey).isNotNull
        assertThat(publicKey.type).isEqualTo("EcdsaSecp256k1VerificationKey2019")
        assertThat(publicKey.controller).isEqualTo(did)
        assertThat(publicKey.publicKeyHash).isEqualTo(address.substring(2).toLowerCase())
        assertThat(publicKey.id).isEqualTo("$did#$keyId#${address.substring(2).toLowerCase()}")

        if (publicKeyHex == null) assertThat(publicKey.publicKeyHex).isNull()
        else assertThat(publicKey.publicKeyHex).isEqualTo(publicKeyHex.substring(2).toLowerCase())
    }

    @ParameterizedTest(name = "DID 형식 validation 테스트: {0}")
    @ValueSource(strings = [
        "did:Zeta:000000000000000000000000000000000000000000000000000000000000112b", // 'Z'
    ])
    fun did_형식이_이상할_때_IllegalArgumentException_발생하는지_확인(did: String) {
        // Given
        val keyId = "Testing"
        val address = "0c65a336fc97d4cf830baeb739153f312cbefcc9"
        val publicKeyHex = "0c65a336fc97d4cf830baeb739153f312cbefcc9"

        // When
        val throwable: Throwable? = catchThrowable {
            PublicKey(did=did, keyId=keyId, address=address, publicKey=publicKeyHex)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(throwable).hasMessageContaining("Invalid DID format: $did")
    }

    @ParameterizedTest(name = "Address 형식 validation 테스트: {0}")
    @ValueSource(strings = [
        "c", // Too short
    ])
    fun address_형식이_이상할_때_IllegalArgumentException_발생하는지_확인(address: String) {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val keyId = "Testing"
        val publicKeyHex = "0c65a336fc97d4cf830baeb739153f312cbefcc9"

        // When
        val throwable: Throwable? = catchThrowable {
            PublicKey(did=did, keyId=keyId, address=address, publicKey=publicKeyHex)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(throwable).hasMessageContaining("Invalid address format: $address")
    }

    @ParameterizedTest(name = "PublicKey 형식 validation 테스트: {0}")
    @ValueSource(strings = [
        "c", // Too short
    ])
    fun publicKeyHex_형식이_이상할_때_IllegalArgumentException_발생하는지_확인(publicKey: String) {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val keyId = "Testing"
        val address = "0c65a336fc97d4cf830baeb739153f312cbefcc9"

        // When
        val throwable: Throwable? = catchThrowable {
            PublicKey(did=did, keyId=keyId, address=address, publicKey=publicKey)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(throwable).hasMessageContaining("Invalid public key hex format: $publicKey")
    }
}