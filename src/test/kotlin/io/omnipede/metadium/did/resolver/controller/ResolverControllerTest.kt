package io.omnipede.metadium.did.resolver.controller

import arrow.core.Either
import io.omnipede.metadium.did.resolver.domain.application.ResolverApplication
import io.omnipede.metadium.did.resolver.domain.application.ResolverError
import io.omnipede.metadium.did.resolver.domain.entity.AssociatedService
import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import io.omnipede.metadium.did.resolver.domain.entity.PublicKey
import io.omnipede.metadium.did.resolver.domain.ports.MetaData
import io.omnipede.metadium.did.resolver.domain.ports.MethodMetaData
import io.omnipede.metadium.did.resolver.domain.ports.ResolverMetaData
import io.omnipede.metadium.did.resolver.system.exception.ErrorCode
import io.omnipede.metadium.did.resolver.system.exception.SystemException
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock

internal class ResolverControllerTest {

    private var resolverApplication: ResolverApplication? = null
    private var resolverController: ResolverController? = null

    @BeforeEach
    fun setup() {
        resolverApplication = mock(ResolverApplication::class.java)
        resolverController = ResolverController(resolverApplication!!)
    }

    @Test
    @DisplayName("Document 와 MetaData 를 반환해야 한다")
    fun should_return_document_and_metadata() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        val publicKeyList: List<PublicKey> = listOf(
            PublicKey(did, "MetaManagementKey", "0x0c65a336fc97d4cf830baeb739153f312cbefcc9"),
        )
        val associatedServiceList: List<AssociatedService> = listOf(
            AssociatedService(did, publicKeyList[0], "https://test.com")
        )
        val document = DidDocument(did, publicKeyList, associatedServiceList)
        val methodMetaData = MethodMetaData("mainnet", "0x42bbff659772231bb63c7c175a1021e080a4cf9d")
        val resolverMetaData = ResolverMetaData("did-meta")
        val metaData = MetaData(methodMetaData, resolverMetaData)

        doReturn(Either.Right(document to metaData))
            .`when`(resolverApplication)!!
            .resolve(did)

        // When
        val dto = resolverController!!.identifiers(did)

        // Then
        assertThat(dto).isNotNull
        assertThat(dto.redirect).isNull()
        assertThat(dto.didDocument).isNotNull
        assertThat(dto.didDocument.context).isEqualTo(document.context)
        assertThat(dto.didDocument.authentication).isEqualTo(document.authenticationList)
        assertThat(dto.didDocument.id).isEqualTo(document.id)
        assertThat(dto.didDocument.publicKey).isEqualTo(document.publicKeyList)
        assertThat(dto.didDocument.service).isEqualTo(document.associatedServiceList)

        assertThat(dto.methodMetadata).isEqualTo(metaData.methodMetaData)
        assertThat(dto.resolverMetadata.cached).isEqualTo(metaData.resolverMetaData.cached)
        assertThat(dto.resolverMetadata.driver).isEqualTo(metaData.resolverMetaData.driver)
        assertThat(dto.resolverMetadata.driverId).isEqualTo(metaData.resolverMetaData.driverId)
        assertThat(dto.resolverMetadata.duration).isEqualTo("${metaData.resolverMetaData.duration} ms")
        assertThat(dto.resolverMetadata.retrieved).isEqualTo(metaData.resolverMetaData.retrieved)
    }

    @Test
    @DisplayName("Document 가 존재하지 않을 때 SystemException 이 발생해야 한다")
    fun should_throw_SystemException_if_document_not_found() {

        // Given
        val did = "did:meta:000000000000000000000000000000000000000000000000000000000000112b"
        doReturn(Either.Left(ResolverError.NotFoundIdentity("Foobar")))
            .`when`(resolverApplication)!!
            .resolve(did)

        // When
        val throwable = catchThrowable {
            resolverController!!.identifiers(did)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(SystemException::class.java)
        val sysExp = throwable as SystemException
        assertThat(sysExp.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
        assertThat(sysExp.message).isEqualTo("Foobar")
    }
}
