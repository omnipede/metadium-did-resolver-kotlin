package io.omnipede.metadium.did.resolver.system.exception

import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Max

/**
 * Exception handler test 용 HTTP controller
 */
@RestController
internal class TempController {

    @GetMapping(value = ["/api/v1/temp"], headers = ["content-type=application/json"])
    fun get(@Valid @RequestParam @Max(1024) id: Int): String {
        if (id == 0)
            throw Exception()
        if (id == 1)
            throw Exception("My custom error")
        if (id == 2)
            throw SystemException(ErrorCode.BAD_REQUEST)
        if (id == 3)
            throw SystemException(ErrorCode.BAD_REQUEST, "Bad request!")
        return "Hello world $id"
    }

    @PostMapping("/api/v1/temp")
    fun post(@Valid @RequestBody dto: TempRequestDTO): String {
        return dto.a ?: "Hello world"
    }
}
