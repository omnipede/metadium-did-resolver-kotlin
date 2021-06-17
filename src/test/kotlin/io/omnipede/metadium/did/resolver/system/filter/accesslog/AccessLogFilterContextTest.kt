package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * WebApplicationContext 를 구축하여 해당 context 상에서 access log filter 가
 * 문제 없이 동작하는지 확인하는 테스트코드
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        TestConfigForAccessLogFilter::class,
        TestControllerForAccessLogFilter::class
    ]
)
@WebAppConfiguration
internal class AccessLogFilterContextTest {

    private var mockMvc: MockMvc? = null

    @Autowired
    private var webAppContext: WebApplicationContext? = null

    @BeforeEach
    fun setup() {

        val configurer = AccessLogFilterConfigurer(enableContentLogging = true)
        val filter = AccessLogFilter(configurer, DefaultAccessLogger())

        // Create mock mvc object for unit testing
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webAppContext!!)
            .addFilter<DefaultMockMvcBuilder>(filter)
            .build()
    }

    @Test
    @DisplayName("GET method 테스트")
    fun test_get() {

        // When
        mockMvc!!.perform(
            get("/api/v1/test")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
    }

    @Test
    @DisplayName("POST method 테스트")
    fun test_post() {

        // Given
        val contents = """
            {
                "a": "Hello world"
            }
        """.trimIndent()

        // When
        mockMvc!!.perform(
            post("/api/v1/test")
                .content(contents)
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isOk)
    }

}

@Configuration
@EnableWebMvc
internal class TestConfigForAccessLogFilter {

    @Bean
    fun testAccessLogFilter(testAccessLogFilterConfigurer: AccessLogFilterConfigurer): FilterRegistrationBean<AccessLogFilter> {
        val filterRegistrationBean = FilterRegistrationBean<AccessLogFilter>()
        filterRegistrationBean.filter = AccessLogFilter(testAccessLogFilterConfigurer, DefaultAccessLogger())
        return filterRegistrationBean
    }

    @Bean
    fun testAccessLogFilterConfigurer(): AccessLogFilterConfigurer {
        return AccessLogFilterConfigurer(
            whiteList = listOf("/api/v1/health", "/favicon.ico"),
            maxContentLength = 10 * 1024 * 1024,
            enableContentLogging = true
        )
    }
}

@RestController
internal class TestControllerForAccessLogFilter {

    @GetMapping("/api/v1/test")
    fun get(): String {
        return "Hello world"
    }

    @PostMapping("/api/v1/test")
    fun post(@RequestBody contents: TestDTO): String {
        return "Hello world ${contents.a}"
    }
}

internal data class TestDTO(
    val a: String
)
