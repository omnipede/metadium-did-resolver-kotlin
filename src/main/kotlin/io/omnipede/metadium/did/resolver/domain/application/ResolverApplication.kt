package io.omnipede.metadium.did.resolver.domain.application

import arrow.core.Either
import arrow.core.getOrHandle
import arrow.core.left
import io.omnipede.metadium.did.resolver.domain.entity.AssociatedService
import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.ports.*
import org.springframework.stereotype.Service

/**
 * DID document resolver 기능을 추상화한 클래스
 */
@Service
class ResolverApplication(
    private val contractService: ContractService,
    private val envService: EnvService,
    private val documentCache: DocumentCache
) {
    /**
     * DID 의 DID document 를 컨트랙으로부터 읽어서 반환하는 메소드
     * @param did Metadium DID. But it should have valid DID format
     * @param noCache 캐시 비활성화 여부
     * @return DID document and server meta data
     */
    fun resolve(did: String, noCache: Boolean): Either<ResolverError, Pair<DidDocument, MetaData>> {

        // Create resolver meta data
        val metaData = envService.loadMetaData()

        // FROM DID, get metaId
        val metadiumDID = MetadiumDID(did)

        // Check whether requested did is inside same network with this resolver
        val currentNetwork = metaData.methodMetaData.network
        if (currentNetwork != metadiumDID.net)
            return Either.Left(ResolverError.DifferentNetwork("This server is DID resolver for $currentNetwork"))

        // noCache 가 true 면 캐시를 조회하지 않고 직접 contract 에서 조회한다
        val document = if (noCache) {
            resolveDocumentFromContract(metadiumDID)
                .getOrHandle { return Either.Left(ResolverError.NotFoundIdentity(it.message!!)) }
        } else {
            resolveDocument(metadiumDID, metaData)
                .getOrHandle { return Either.Left(ResolverError.NotFoundIdentity(it.message!!)) }
        }

        // Mark metadata that resolving process is end
        metaData.endResolving()

        // Return document and metadata
        return Either.Right(document to metaData)
    }

    /**
     * 캐시된 document 를 삭제하는 메소드. 캐시된 document 가 없으면 false 를 반환하고 삭제에 성공하면 true 를 반환한다
     * @param did DID for document
     * @return 캐시된 document 가 없으면 false 를 반환하고 삭제에 성공하면 true 를 반환한다
     */
    fun deleteDocumentFromCache(did: String): Boolean {

        // FROM DID, get metaId
        val metadiumDID = MetadiumDID(did)

        // 캐시에서 삭제한다
        return documentCache.delete(metadiumDID)
    }

    /**
     * DID document 를 반환하는 메소드. 만약 캐시된 document 가 있으면 캐시에서 찾고,
     * 캐시된 document 가 없다면 contract 에서 직접 찾는다
     * @param metadiumDID DID of document
     * @param metaData Resolver 메타데이터 객체
     */
    private fun resolveDocument(metadiumDID: MetadiumDID, metaData: MetaData): Either<NotFoundIdentityException, DidDocument> {

        // 먼저 캐시에서 document 를 찾는다
        val cachedDocument = documentCache.find(metadiumDID)
        // 찾는데 성공했다면 해당 document 를 반환
        val document = if (cachedDocument.isPresent) {
            // 메타데이타에 캐시되었음을 표기
            metaData.markCached()
            cachedDocument.get()
        } else {
            // 캐시 되지 않았다면 contract 에서 찾는다
            resolveDocumentFromContract(metadiumDID)
                .getOrHandle { return Either.Left(it) }
        }
        return Either.Right(document)
    }

    /**
     * DID 의 DID document 를 컨트랙으로부터 읽어서 반환하는 메소드
     * @param metadiumDID Metadium DID. But it should have valid DID format.
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

        // Save document to cache
        documentCache.save(document)

        return Either.Right(document)
    }
}
