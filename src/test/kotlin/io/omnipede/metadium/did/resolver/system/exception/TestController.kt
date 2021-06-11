package io.omnipede.metadium.did.resolver.system.exception

import org.hibernate.validator.constraints.Range
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.jvm.Throws

/**
 * Exception handler test 용 HTTP controller
 */
@RestController
@RequestMapping("/api/v1")
@Validated
internal class TestController {

    @GetMapping(value = ["/temp"], headers = ["content-type=application/json"])
    @Throws(Exception::class)
    fun get(@RequestParam @Range(min=0, max=10) id: Int): String {
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

    @PostMapping("/temp")
    fun post(@Valid @RequestBody dto: TestRequestDTO): String {
        return dto.a ?: "Hello world"
    }
}
