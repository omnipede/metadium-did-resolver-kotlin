package io.omnipede.metadium.did.resolver.infra.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.omnipede.metadium.did.resolver.domain.entity.DidDocument
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
internal class CaffeineCacheBean(
    private val cacheProperty: CacheProperty
) {

    @Bean
    fun cache(): Cache<String, DidDocument> {

        return Caffeine.newBuilder()
            .maximumSize(cacheProperty.maximumSize!!)
            .expireAfterWrite(cacheProperty.duration!!, TimeUnit.SECONDS)
            .build()
    }
}
