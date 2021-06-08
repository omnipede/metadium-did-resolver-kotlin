package io.omnipede.metadium.did.resolver.system.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

/**
 * Property 파일에서 'resolver' prefix 상의 환경변수를 읽어오는 클래스
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "resolver")
@Validated
class ResolverProperty {

    @NotEmpty
    lateinit var driverId: String
}