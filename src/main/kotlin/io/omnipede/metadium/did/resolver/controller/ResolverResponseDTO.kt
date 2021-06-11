package io.omnipede.metadium.did.resolver.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import io.omnipede.metadium.did.resolver.domain.ports.MetaData

/**
 * Response DTO
 */
@JsonPropertyOrder("redirect", "didDocument", "resolverMetadata", "methodMetadata")
class ResolverResponseDTO(
    didDocument: DidDocument,
    metadata: MetaData
) {
    val redirect: String? = null

    val didDocument = DocumentDTO(didDocument)

    val resolverMetadata = ResolverMetaDataDTO(metadata)

    val methodMetadata = metadata.methodMetaData
}

/**
 * DID document DTO
 */
@JsonPropertyOrder("@context", "id", "publicKey", "authentication", "service")
class DocumentDTO(
    document: DidDocument
) {
    @JsonProperty("@context")
    val context: String = document.context

    val id: String = document.id

    val publicKey = document.publicKeyList

    val authentication = document.authenticationList

    val service = document.associatedServiceList
}

/**
 * Resolver metadata DTO
 */
@JsonPropertyOrder("driverId", "driver", "retrieved", "duration", "cached")
class ResolverMetaDataDTO(
    metaData: MetaData
) {

    val driverId = metaData.resolverMetaData.driverId

    val cached = metaData.resolverMetaData.cached

    val driver = metaData.resolverMetaData.driver

    val retrieved = metaData.resolverMetaData.retrieved

    val duration = "${metaData.resolverMetaData.duration} ms"
}
