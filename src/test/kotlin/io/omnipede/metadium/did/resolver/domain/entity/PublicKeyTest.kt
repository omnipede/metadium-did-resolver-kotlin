package io.omnipede.metadium.did.resolver.domain.entity

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class PublicKeyTest {

    @Test
    @DisplayName("DID, address 이용해서 PublicKey 객체 생성")
    fun publicKey_constructor_test_2() {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val keyId = "Testing"
        val address = "0x0C65a336fc97d4cf830baeb739153f312cbefcc9"

        // When
        val publicKey = PublicKey(did=did, keyId=keyId, address=address)

        // Then
        assertThat(publicKey).isNotNull
        assertThat(publicKey.type).isEqualTo("EcdsaSecp256k1VerificationKey2019")
        assertThat(publicKey.controller).isEqualTo(did)
        assertThat(publicKey.publicKeyHash).isEqualTo(address.substring(2).toLowerCase())
        assertThat(publicKey.id).isEqualTo("$did#$keyId#${address.substring(2).toLowerCase()}")
        assertThat(publicKey.publicKeyHex).isNull()
    }

    @Test
    @DisplayName("DID, address, PublicKeyHex 이용해서 PublicKey 객체 생성")
    fun publicKey_constructor_test_1() {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val keyId = "Testing"
        val address = "0C65a336fc97d4cf830baeb739153f312cbefcc9"
        val publicKeyHex = "0X48f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83"

        // When
        val publicKey = PublicKey(did=did, keyId=keyId, address=address, publicKey=publicKeyHex)

        // Then
        assertThat(publicKey).isNotNull
        assertThat(publicKey.type).isEqualTo("EcdsaSecp256k1VerificationKey2019")
        assertThat(publicKey.controller).isEqualTo(did)
        assertThat(publicKey.publicKeyHash).isEqualTo(address.toLowerCase())
        assertThat(publicKey.id).isEqualTo("$did#$keyId#${address.toLowerCase()}")
        assertThat(publicKey.publicKeyHex).isEqualTo(publicKeyHex.substring(2).toLowerCase())
    }

    @Test
    @DisplayName("DID 형식 validation 테스트")
    fun did_형식이_이상할_때_IllegalArgumentException_발생하는지_확인() {
        // Given
        val did = "did:Zeta:000000000000000000000000000000000000000000000000000000000000112b"
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

    @Test
    @DisplayName("Address 형식 validation 테스트")
    fun address_형식이_이상할_때_IllegalArgumentException_발생하는지_확인() {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val keyId = "Testing"
        val address = "c"
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

    @Test
    @DisplayName("PublicKey 형식 validation 테스트")
    fun publicKeyHex_형식이_이상할_때_IllegalArgumentException_발생하는지_확인() {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val keyId = "Testing"
        val address = "0c65a336fc97d4cf830baeb739153f312cbefcc9"
        val publicKey = "c"

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
