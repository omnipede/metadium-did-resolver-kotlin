package io.omnipede.metadium.did.resolver.system.exception

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

internal class GlobalExceptionHandlerTest {

    private var mockMvc: MockMvc? = null

    @BeforeEach
    fun setup() {

        // Create mock mvc object for unit testing
        val tempController = TempController()
        val globalExceptionHandler = GlobalExceptionHandler()
        mockMvc = MockMvcBuilders
            .standaloneSetup(tempController)
            // Mock mvc 의 dispatcher servlet 에 대해서 handler 가 존재하지 않을 때
            // NoHandlerFoundException 을 발생시키도록 설정
            .addDispatcherServletCustomizer<StandaloneMockMvcBuilder> {
                it.setThrowExceptionIfNoHandlerFound(true)
            }
            .setControllerAdvice(globalExceptionHandler)
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
