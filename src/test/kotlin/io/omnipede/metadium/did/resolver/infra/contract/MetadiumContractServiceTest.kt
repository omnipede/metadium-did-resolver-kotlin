package io.omnipede.metadium.did.resolver.infra.contract

import arrow.core.Either
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
import io.omnipede.metadium.did.resolver.domain.ports.NotFoundIdentityException
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.*

internal class MetadiumContractServiceTest {

    private var keyManagerService: KeyManagerService? = null
    private var metadiumContractService: MetadiumContractService? = null

    @BeforeEach
    fun setup() {
        keyManagerService = mock(KeyManagerService::class.java)
        metadiumContractService = MetadiumContractService(keyManagerService!!)
    }

    @Test
    @DisplayName("PublicKey 리스트를 반환하는지 확인")
    fun should_return_public_key_list() {

        // Given
        val metadiumDID = MetadiumDID("did:meta:000000000000000000000000000000000000000000000000000000000000112b")
        val publicKey = PublicKey(metadiumDID.toString(), UUID.randomUUID().toString(), "0x0c65a336fc97d4cf830baeb739153f312cbefcc9")

        val mockKeyManager = mock(KeyManager::class.java)
        doReturn(listOf(publicKey))
            .`when`(mockKeyManager)
            .findPublicKeys()

        doReturn(listOf(publicKey))
            .`when`(mockKeyManager)
            .findServiceKeys()

        doReturn(Either.Right(mockKeyManager))
            .`when`(keyManagerService)!!
            .createKeyManager(metadiumDID)

        // When
        val result = metadiumContractService!!.findPublicKeyList(metadiumDID)

        // Then
        assertThat(result).isNotNull
        result.map {
            assertThat(it).isNotNull
            assertThat(it.publicKeyList).usingRecursiveComparison().isEqualTo(listOf(publicKey))
            assertThat(it.serviceKeyList).usingRecursiveComparison().isEqualTo(listOf(publicKey))
        }
    }

    @Test
    @DisplayName("Identity 가 존재하지 않을 경우 NotFoundIdentityException 반환해야 한다")
    fun should_return_NotFoundIdentityException_when_identity_is_not_found() {

        // Given
        val metadiumDID = MetadiumDID("did:meta:000000000000000000000000000000000000000000000000000000000000112b")
        doReturn(Either.Left(NotFoundIdentityException("Foo bar")))
            .`when`(keyManagerService)!!
            .createKeyManager(metadiumDID)

        // When
        val result = metadiumContractService!!.findPublicKeyList(metadiumDID)

        // Then
        assertThat(result).isNotNull
        result.mapLeft {
            assertThat(it).isNotNull
        }
    }
}
