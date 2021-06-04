package io.omnipede.metadium.did.resolver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ResolverApplication

fun main(args: Array<String>) {
    runApplication<ResolverApplication>(*args)
}