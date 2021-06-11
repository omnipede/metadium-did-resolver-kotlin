package io.omnipede.metadium.did.resolver.domain.application

import arrow.core.Either
import arrow.core.getOrHandle
import arrow.core.left
import io.omnipede.metadium.did.resolver.domain.ports.ContractService
import io.omnipede.metadium.did.resolver.domain.ports.EnvService
import io.omnipede.metadium.did.resolver.domain.ports.MetaData
import io.omnipede.metadium.did.resolver.domain.entity.AssociatedService
import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.ports.NotFoundIdentityException
import org.springframework.stereotype.Service

/**
 * DID document resolver 기능을 추상화한 클래스
 */
@Service
class ResolverApplication(
    private val contractService: ContractService,
    private val envService: EnvService
) {
    /**
     * DID 의 DID document 를 컨트랙으로부터 읽어서 반환하는 메소드
     * @param did Metadium DID. But it should have valid DID format.
     * @return DID document and server meta data
     */
    fun resolve(did: String): Either<ResolverError, Pair<DidDocument, MetaData>> {

        // Create resolver meta data
        val metaData = envService.loadMetaData()

        // FROM DID, get metaId
        val metadiumDID = MetadiumDID(did)

        // Check whether requested did is inside same network with this resolver
        val currentNetwork = envService.getNetwork()
        if (currentNetwork != metadiumDID.net)
            return Either.Left(ResolverError.DifferentNetwork("This server is DID resolver for $currentNetwork"))

        // Resolve did document
        val document = resolveDocumentFromContract(metadiumDID)
            .getOrHandle { return Either.Left(ResolverError.NotFoundIdentity(it.message!!)) }

        // Mark metadata that resolving process is end
        metaData.endResolving()

        // Return document and metadata
        return Either.Right(document to metaData)
    }

    /**
     * DID 의 DID document 를 컨트랙으로부터 읽어서 반환하는 메소드
     * @param did Metadium DID. But it should have valid DID format.
     */
    private fun resolveDocumentFromContract(metadiumDID: MetadiumDID): Either<NotFoundIdentityException, DidDocument> {

        // Find publicKey list using metadiumDID
        val publicKeyListResult = contractService.findPublicKeyList(metadiumDID)
            .getOrHandle { return Either.Left(it) }

        val metaManagementKeyList = publicKeyListResult.publicKeyList

        // Create service using identity hub data
        val serviceList: MutableList<AssociatedService> = mutableListOf()
        if (metaManagementKeyList.isNotEmpty())
            serviceList += envService.createService(metaManagementKeyList[0])

        // Create document and return
        val document = DidDocument(
            did=metadiumDID.toString(),
            publicKeyList = publicKeyListResult.toList(),
            serviceList
        )

        return Either.Right(document)
    }
}
