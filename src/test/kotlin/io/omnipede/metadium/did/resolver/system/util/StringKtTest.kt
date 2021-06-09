package io.omnipede.metadium.did.resolver.system.util

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class StringKtTest {

    @ParameterizedTest(name = "올바른 DID 형식 테스트: {0}")
    @ValueSource(strings = [
        "did:meta:000000000000000000000000000000000000000000000000000000000000112b",
        "did:meta:c00000000000000000000000000000000000000000000000000000000000112c",
        "did:meta:mainnet:000000000000000000000000000000000000000000000000000000000000112b",
        "did:meta:testnet:000000000000000000000000000000000000000000000000000000000000112b",
    ])
    fun DID_format_test(did: String) {
        // Given

        // When
        val result = did.isValidDid()

        // Then
        assertThat(result).isTrue
    }

    @ParameterizedTest(name = "틀린 DID 형식 테스트: {0}")
    @ValueSource(strings = [
        "did:Zeta:000000000000000000000000000000000000000000000000000000000000112b", // 'Z'
        "did:000000000000000000000000000000000000000000000000000000000000112b", // 'mainnet|testnet' omitted
        "did:meta:000000000000000000000000000000000000000000000000000000000000112", // Too short
        "did:meta:000000000000000000000000000000000000000000000000000000000000112ee", // Too long
        "did:meta:00000000000000000000000000000000000000000000000000000000000011g" // 'g'
    ])
    fun wrong_DID_format_test(did: String) {
        // Given

        // When
        val result = did.isValidDid()

        // Then
        assertThat(result).isFalse
    }

    @ParameterizedTest(name = "올바른 metadium address 형식 테스트: {0}")
    @ValueSource(strings = [
        "0x0c65a336fc97d4cf830baeb739153f312cbefcc9",
        "0X0c65a336fc97d4cf830baeb739153f312cbefcc9",
        "0c65a336fc97d4cf830baeb739153f312cbefcc9",
        "0x0C65a336fc97d4cf830baeb739153f312cbefcc9"
    ])
    fun metadiumAddress_format_test(address: String) {
        // Given

        // When
        val result = address.isValidMetadiumAddress()

        // Then
        assertThat(result).isTrue
    }

    @ParameterizedTest(name = "틀린 metadium address 형식 테스트: {0}")
    @ValueSource(strings = [
        "c", // Too short
        "0x",
        "0X",
        "0c65a336fc97d4cf830baeb739153f312cbefcc", // Too short
        "0X65a336fc97d4cf830baeb739153f312cbefcc9", // Too short
        "0Z65a336fc97d4cf830baeb739153f312cbefcc9", // 'Z'
        "0c65a336fc97d4cf830baeb739153f312cbefcc91" // Too long
    ])
    fun wrong_metadiumAddress_format_test(address: String) {
        // Given

        // When
        val result = address.isValidMetadiumAddress()

        // Then
        assertThat(result).isFalse
    }

    @ParameterizedTest(name = "올바른 metadium public key hex 형식 테스트: {0}")
    @ValueSource(strings = [
        "49f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83",
        "0x49f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83",
        "0X49f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83",
        "0XA8f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83"
    ])
    fun metadium_public_key_hex_format_test(s: String) {

        // Given

        // When
        val result = s.isValidMetadiumPublicKeyHex()

        // Then
        assertThat(result).isTrue
    }

    @ParameterizedTest(name = "올바른 metadium public key hex 형식 테스트: {0}")
    @ValueSource(strings = [
        "4",
        "0x",
        "0X",
        "0XA8f78d9ef20ede7f29702b6c30236482e35528adb1be2"
    ])
    fun wrong_metadium_public_key_hex_format_test(s: String) {

        // Given

        // When
        val result = s.isValidMetadiumPublicKeyHex()

        // Then
        assertThat(result).isFalse
    }

    @ParameterizedTest(name = "올바른 web url 형식 테스트: {0}")
    @ValueSource(strings = [
        "https://datahub.metadium.com",
        "http://datahub.metadium.com",
        "http://dddTest.host.io",
        "http://test.io",
    ])
    fun webUrl_format_test(url: String) {
        // Given

        // When
        val result = url.isValidWebUrl()

        // Then
        assertThat(result).isTrue
    }

    @ParameterizedTest(name = "틀린 web url 형식 테스트: {0}")
    @ValueSource(strings = [
        "ftp://datahub.metadium.com",
        "https:/datahub.metadium.com",
        "https://datahub",
        "https://dat#hub.metadium.com",
        "https://dat!hub.metadium.com",
    ])
    fun wrong_webUrl_format_test(url: String) {
        // Given

        // When
        val result = url.isValidWebUrl()

        // Then
        assertThat(result).isFalse
    }

    @ParameterizedTest(name = "16진수 문자열 정규화 테스트: {0}")
    @ValueSource(strings = [
        "0x49f78d9ef20ede7f29702b6c30236482e35528adb1be25e0cea5c55a6337b0adc3e9d12c75bb46e6b7a589c7cd538a9d47a1cadca37286d249be01b83a95db83",
        "0x45",
        "0X4AbcE",
    ])
    fun hex_string_normalization_test(s: String) {

        // Given

        // When
        val t = s.toNormalizedHex()

        // Then
        assertThat(t).isEqualTo(s.removePrefix("0x").removePrefix("0X").toLowerCase())
    }
}
