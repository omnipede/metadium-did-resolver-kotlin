package io.omnipede.metadium.did.resolver.system.config

import io.omnipede.metadium.did.resolver.system.filter.accesslog.AccessLogFilter
import io.omnipede.metadium.did.resolver.system.filter.accesslog.AccessLogFilterConfigurer
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FilterConfig {

    @Bean
    fun accessLogFilter(accessLogFilterConfigurer: AccessLogFilterConfigurer): FilterRegistrationBean<AccessLogFilter> {
        val filterRegistrationBean = FilterRegistrationBean<AccessLogFilter>()
        filterRegistrationBean.filter = AccessLogFilter(accessLogFilterConfigurer)
        return filterRegistrationBean
    }

    @Bean
    fun accessLogFilterConfigurer(): AccessLogFilterConfigurer {
        return AccessLogFilterConfigurer(
            whiteList = listOf("/api/v1/health", "/favicon.ico"),
            maxContentLength = 10 * 1024 * 1024,
            enableContentLogging = true
        )
    }
}
