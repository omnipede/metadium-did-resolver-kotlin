package io.omnipede.metadium.did.resolver.system.config

import io.omnipede.metadium.did.resolver.system.util.WebUrl
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

/**
 * Spring property file 의 identity hub property 를 읽어오는 클래스
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "identityhub")
@Validated
class IdentityHubProperty {

    @NotEmpty
    lateinit var id: String

    @NotEmpty
    @WebUrl
    lateinit var url: String
}