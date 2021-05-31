package io.omnipede.metadium.did.resolver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
open class ResolverApplication

fun main(args: Array<String>) {
    runApplication<ResolverApplication>(*args)
}

@RestController
class TempController {

    @GetMapping("/api/v1/hello")
    fun index(): List<Message> {

        return listOf(
            Message("1", "Hello"),
            Message("2", "Kotlin")
        )
    }
}

data class Message(val id: String, val contents: String)