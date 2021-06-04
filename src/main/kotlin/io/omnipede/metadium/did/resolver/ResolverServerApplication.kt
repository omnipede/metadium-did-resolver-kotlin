package io.omnipede.metadium.did.resolver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ResolverServerApplication

fun main(args: Array<String>) {
    runApplication<ResolverServerApplication>(*args)
}