package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletResponse
import java.lang.RuntimeException
import javax.servlet.http.HttpServletResponse

internal class CachingResponseWrapperTest {

    @Test
    @DisplayName("Response body 를 캐싱해야 한다")
    fun content_caching_test() {

        // Given
        val mockHttpServletResponse = MockHttpServletResponse()
        val cachingResponseWrapper = CachingResponseWrapper(mockHttpServletResponse)
        val content = "Hello world"

        // When
        val httpServletResponse = cachingResponseWrapper as HttpServletResponse
        val outputStream = httpServletResponse.outputStream
        val isReady = outputStream.isReady

        outputStream.write(content.toByteArray())
        val cached = cachingResponseWrapper.getBody(128)

        outputStream.close()

        outputStream.write(content.toByteArray(), 0, content.length)
        val secondCached = cachingResponseWrapper.getBody(128)

        outputStream.flush()
        outputStream.close()

        for (byte in content.toByteArray()) {
            outputStream.write(byte.toInt())
        }
        val thirdCached = cachingResponseWrapper.getBody(128)

        // Then
        assertThat(cached).isEqualTo(content)
        assertThat(content).isEqualTo(secondCached)
        assertThat(secondCached).isEqualTo(thirdCached)
        assertThat(isReady).isTrue
    }

    @Test
    @DisplayName("Response body 가 너무 길면 에러 메시지를 반환해야 한다")
    fun should_return_error_message_when_content_is_too_long() {

        // Given
        val mockHttpServletResponse = MockHttpServletResponse()
        val cachingResponseWrapper = CachingResponseWrapper(mockHttpServletResponse)
        val content = "Hello world"

        // When
        val httpServletResponse = cachingResponseWrapper as HttpServletResponse
        val outputStream = httpServletResponse.outputStream

        outputStream.write(content.toByteArray())
        val cached = cachingResponseWrapper.getBody(1)

        // Then
        assertThat(cached).isNotEqualTo(content)
        assertThat(cached).isEqualTo("TOO LONG CONTENTS")
    }

    @Test
    @DisplayName("setWriterListener() 가 호출 될 경우 RuntimeException 이 발생해야 한다")
    fun should_throw_RuntimeException_when_setWriterListener_is_called() {

        // Given
        val mockHttpServletResponse = MockHttpServletResponse()
        val cachingResponseWrapper = CachingResponseWrapper(mockHttpServletResponse)

        // When
        val httpServletResponse = cachingResponseWrapper as HttpServletResponse
        val outputStream = httpServletResponse.outputStream

        val throwable = catchThrowable {
            outputStream.setWriteListener(null)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
        assertThat(throwable).hasMessage("Not implemented yet")
    }
}
