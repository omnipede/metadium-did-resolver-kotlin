package io.omnipede.metadium.did.resolver.infra.cache

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "cache")
@Validated
internal class CacheProperty {

    // Cache 지속시간
    @NotNull
    @Min(0)
    var duration: Long? = null

    // Cache entry 최대 개수. 단위: 초
    @NotNull
    @Min(0)
    var maximumSize: Long? = null
}
