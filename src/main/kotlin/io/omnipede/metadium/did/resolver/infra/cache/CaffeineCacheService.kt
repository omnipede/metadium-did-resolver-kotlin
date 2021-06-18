package io.omnipede.metadium.did.resolver.infra.cache

import com.github.benmanes.caffeine.cache.Cache
import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import io.omnipede.metadium.did.resolver.domain.entity.MetadiumDID
import io.omnipede.metadium.did.resolver.domain.ports.DocumentCache
import org.springframework.stereotype.Service
import java.util.*

/**
 * Simple in-memory cache service implemented using Caffeine cache
 */
@Service
internal class CaffeineCacheService(
    private val cache: Cache<String, DidDocument>
): DocumentCache {

    override fun find(did: MetadiumDID): Optional<DidDocument> {

        val cached = cache.getIfPresent(did.toString())
            ?: return Optional.empty()

        return Optional.of(cached)
    }

    override fun save(document: DidDocument) {

        val did = document.id
        cache.put(did, document)
    }

    override fun delete(did: MetadiumDID): Boolean {

        cache.getIfPresent(did.toString()) ?: return false
        cache.invalidate(did.toString())
        return true
    }
}
