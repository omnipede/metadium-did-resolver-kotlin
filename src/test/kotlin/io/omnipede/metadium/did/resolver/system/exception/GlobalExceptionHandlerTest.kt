package io.omnipede.metadium.did.resolver.system.exception

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import javax.validation.Validation
import javax.validation.ValidatorFactory

@ExtendWith(SpringExtension::class)
// 테스트할 때 사용할 context configuration
@ContextConfiguration(
    classes = [
        TestConfig::class,
        // 테스트 시 사용할 http controller
        TestController::class,
        // 테스트하고자 하는 exception handler
        GlobalExceptionHandler::class
    ])
@WebAppConfiguration
internal class GlobalExceptionHandlerTest {

    private var mockMvc: MockMvc? = null

    @Autowired
    private var webAppContext: WebApplicationContext? = null

    @BeforeEach
    fun setup() {

        // Create mock mvc object for unit testing
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webAppContext!!)
            // Mock mvc 의 dispatcher servlet 에 대해서 handler 가 존재하지 않을 때
            // NoHandlerFoundException 을 발생시키도록 설정
            .addDispatcherServletCustomizer<DefaultMockMvcBuilder> {
                it.setThrowExceptionIfNoHandlerFound(true)
            }
            .build()
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 헨들링 테스트")
    fun handle_MethodArgumentNotValidException_test() {

        // Given
        val requestDTO = """
            {
                "a": "",
                "b": 123
            }
        """.trimIndent()

        // When
        mockMvc!!.perform(
            post("/api/v1/temp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDTO)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    @DisplayName("ConstraintViolationException 헨들링 테스트")
    fun handle_ConstraintViolationException_test() {

        // Given

        // When
        mockMvc!!.perform(
            get("/api/v1/temp?id=1025")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }

    @Test
    @DisplayName("MissingServletRequestParameterException 헨들링 테스트")
    fun handle_MissingServletRequestParameterException_test() {

        // Given

        // When
        mockMvc!!.perform(
            get("/api/v1/temp")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 헨들링 테스트")
    fun handle_HttpMessageNotReadableException_test() {

        // Given
        val requestDTO = """
            {
                "a": "" ,,,,,  <-- Invalid JSON
            }
        """.trimIndent()

        // When
        mockMvc!!.perform(
            post("/api/v1/temp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDTO)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    @DisplayName("NoHandlerFoundException 헨들링 테스트")
    fun handle_NoHandlerFoundException_test() {

        // Given

        // When
        mockMvc!!.perform(
            get("/api/v1/missing")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isNotFound)
            .andReturn()
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException 헨들링 테스트")
    fun handle_HttpRequestMethodNotSupportedException_test() {
        // Given

        // When
        mockMvc!!.perform(
            put("/api/v1/temp")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    @DisplayName("HttpMediaTypeNotSupportedException 헨들링 테스트")
    fun handle_HttpMediaTypeNotSupportedException_test() {
        // Given

        // When
        mockMvc!!.perform(
            get("/api/v1/temp?id=12345")
                .contentType(MediaType.TEXT_EVENT_STREAM)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    @DisplayName("SystemException 헨들링 테스트")
    fun handle_SystemException_test() {

        // Given

        // When
        mockMvc!!.perform(
            get("/api/v1/temp?id=2")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        // When
        mockMvc!!.perform(
            get("/api/v1/temp?id=3")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    @DisplayName("Exception 헨들링 테스트")
    fun handle_Exception_test() {
        // Given

        // When
        mockMvc!!.perform(
            get("/api/v1/temp?id=1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isInternalServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        // When
        mockMvc!!.perform(
            get("/api/v1/temp?id=0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isInternalServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }

    @Test
    @DisplayName("Log 레벨이 DEBUG 가 아닐 경우")
    fun when_log_level_is_not_debug() {

        // Given
        val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val logger: Logger = loggerContext.getLogger(GlobalExceptionHandler::class.java)
        logger.level = Level.INFO

        // When
        mockMvc!!.perform(
            get("/api/v1/temp")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }
}
