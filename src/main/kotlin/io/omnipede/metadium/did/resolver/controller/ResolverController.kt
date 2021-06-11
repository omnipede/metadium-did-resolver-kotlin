package io.omnipede.metadium.did.resolver.controller

import arrow.core.getOrHandle
import io.omnipede.metadium.did.resolver.domain.application.ResolverApplication
import io.omnipede.metadium.did.resolver.system.exception.ErrorCode
import io.omnipede.metadium.did.resolver.system.exception.SystemException
import io.omnipede.metadium.did.resolver.system.util.MetadiumDID
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/1.0")
@Validated
class ResolverController(
    private val resolverApplication: ResolverApplication
) {

    @GetMapping("/identifiers/{did}")
    fun identifiers(@PathVariable @MetadiumDID did: String): ResolverResponseDTO {

        val (document, metadata) = resolverApplication.resolve(did)
            .getOrHandle {
                throw SystemException(ErrorCode.NOT_FOUND, it.reason)
            }

        return ResolverResponseDTO(document, metadata)
    }
}
