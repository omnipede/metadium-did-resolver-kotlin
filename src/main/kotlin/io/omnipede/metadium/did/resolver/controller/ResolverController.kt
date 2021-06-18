package io.omnipede.metadium.did.resolver.controller

import arrow.core.getOrHandle
import io.omnipede.metadium.did.resolver.domain.application.ResolverApplication
import io.omnipede.metadium.did.resolver.system.exception.ErrorCode
import io.omnipede.metadium.did.resolver.system.exception.SystemException
import io.omnipede.metadium.did.resolver.system.util.MetadiumDID
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/1.0/identifiers")
@Validated
class ResolverController(
    private val resolverApplication: ResolverApplication
) {

    @GetMapping("/{did}")
    fun identifiers(
        @PathVariable @MetadiumDID did: String,
        @RequestHeader(name = "no-cache", required = false, defaultValue = "false") noCache: Boolean
    ): ResolverResponseDTO {

        val (document, metadata) = resolverApplication.resolve(did, noCache)
            .getOrHandle {
                throw SystemException(ErrorCode.NOT_FOUND, it.reason)
            }

        return ResolverResponseDTO(document, metadata)
    }

    @DeleteMapping("/{did}")
    fun purge(
        @PathVariable @MetadiumDID did: String
    ): Map<String, String> {

        val cacheFound = resolverApplication.deleteDocumentFromCache(did)
            .getOrHandle {
                throw SystemException(ErrorCode.NOT_FOUND, it.reason)
            }

        if (cacheFound)
            return mapOf(
                "success" to "true",
                 "message" to "Cache purging of '${did}' has been completed"
            )

        return mapOf(
            "success" to "true",
            "message" to "Not found DID data(${did}) in cache."
        )
    }
}
