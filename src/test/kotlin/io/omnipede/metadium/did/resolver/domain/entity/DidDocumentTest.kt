package io.omnipede.metadium.did.resolver.domain.entity

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.stream.Collectors

internal class DidDocumentTest {

    @Test
    @DisplayName("DID 이용해서 did document 객체 생성")
    fun didDocument_constructor_test_1() {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"

        // When
        val didDocument = DidDocument(did)

        // Then
        assertThat(didDocument).isNotNull
        assertThat(didDocument.id).isEqualTo(did)
        assertThat(didDocument.context).isEqualTo("https://w3id.org/did/v0.11")
        assertThat(didDocument.publicKeyList.size).isEqualTo(0)
        assertThat(didDocument.authenticationList.size).isEqualTo(0)
        assertThat(didDocument.associatedServiceList.size).isEqualTo(0)
    }

    @Test
    @DisplayName("DID, publicKey 리스트 이용해서 did document 객체 생성")
    fun didDocument_constructor_test_2() {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val keyId = "Testing"
        val publicKeyList: List<PublicKey> = listOf(
            PublicKey(did, keyId, "0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
            PublicKey(did, keyId, "0x0c65a336fc97d4cf830baeb739153f312cbefcc9")
        )

        // When
        val didDocument = DidDocument(did, publicKeyList)

        // Then
        assertThat(didDocument).isNotNull
        assertThat(didDocument.id).isEqualTo(did)
        assertThat(didDocument.context).isEqualTo("https://w3id.org/did/v0.11")
        assertThat(didDocument.publicKeyList.size).isEqualTo(2)
        assertThat(didDocument.authenticationList.size).isEqualTo(2)
        assertThat(didDocument.associatedServiceList.size).isEqualTo(0)
    }

    @Test
    @DisplayName("DID, publicKey, service 리스트를 이용해서 did document 객체 생성")
    fun didDocument_constructor_test_3() {
        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val keyId = "Testing"
        val publicKeyList: List<PublicKey> = listOf(
            PublicKey(did, keyId, "0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
            PublicKey(did, keyId, "0x0c65a336fc97d4cf830baeb739153f312cbefcc9")
        )
        val serviceEndPoint = "https://testing.metadium.com"
        val associatedServiceList: List<AssociatedService> = publicKeyList.stream().map {
            AssociatedService(did=did, it, url=serviceEndPoint)
        }.collect(Collectors.toList())

        // When
        val didDocument = DidDocument(did=did, publicKeyList=publicKeyList, associatedServiceList=associatedServiceList)

        // Then
        assertThat(didDocument).isNotNull
        assertThat(didDocument.id).isEqualTo(did)
        assertThat(didDocument.context).isEqualTo("https://w3id.org/did/v0.11")
        assertThat(didDocument.publicKeyList.size).isEqualTo(2)
        assertThat(didDocument.authenticationList.size).isEqualTo(2)
        assertThat(didDocument.associatedServiceList.size).isEqualTo(2)
    }

    @Test
    @DisplayName("틀린 DID 형식인 경우 IllegalArgumentException 이 발생")
    fun did_format_validation() {
        // Given
        val did = "did:meta:::::000000000000000000000000000000000000000000000000000000000000112b"

        // When
        val throwable: Throwable? = catchThrowable {
            DidDocument(did=did)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(throwable).hasMessageContaining("Invalid DID format: $did")
    }
}
