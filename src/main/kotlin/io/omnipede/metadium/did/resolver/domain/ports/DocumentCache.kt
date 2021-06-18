package io.omnipede.metadium.did.resolver.domain.ports

import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import java.util.*

interface DocumentCache {

    fun find(did: MetadiumDID): Optional<DidDocument>
    fun save(document: DidDocument)
    fun delete(did: MetadiumDID): Boolean
}
