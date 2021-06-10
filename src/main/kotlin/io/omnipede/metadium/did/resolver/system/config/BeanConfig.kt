package io.omnipede.metadium.did.resolver.system.config

import io.undertow.servlet.api.DeploymentInfo
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class BeanConfig {

    /**
     * Undertow bean 설정
     */
    @Bean
    fun embeddedServletContainerFactory(): UndertowServletWebServerFactory? {
        val factory = UndertowServletWebServerFactory()
        factory.addDeploymentInfoCustomizers(UndertowDeploymentInfoCustomizer { deploymentInfo: DeploymentInfo ->
            deploymentInfo.isAllowNonStandardWrappers = true
        })
        return factory
    }
}
