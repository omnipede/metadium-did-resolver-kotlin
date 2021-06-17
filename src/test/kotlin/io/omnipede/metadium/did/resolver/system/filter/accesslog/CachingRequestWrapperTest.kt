package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.apache.commons.io.IOUtils
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import java.io.ByteArrayOutputStream
import java.util.stream.Collectors
import javax.servlet.ReadListener
import javax.servlet.http.HttpServletRequest

internal class CachingRequestWrapperTest {

    @Test
    @DisplayName("Content 를 캐싱해야 한다")
    fun content_caching_test() {

        // Given
        val contents = "Hello world"
        val mockHttpServletRequest = MockHttpServletRequest()
        mockHttpServletRequest.setContent(contents.toByteArray())
        val contentCachingRequestWrapper = CachingRequestWrapper(mockHttpServletRequest)

        // When
        val httpServletRequest: HttpServletRequest = contentCachingRequestWrapper

        val inputStream = httpServletRequest.inputStream
        val isReady = inputStream.isReady
        val isFinishedBeforeFinish = inputStream.isFinished
        val cached = IOUtils.toString(inputStream)
        val isFinishedAfterFinish = inputStream.isFinished

        val secondInputStream = httpServletRequest.inputStream
        val secondCached = IOUtils.toString(secondInputStream)
        val reader = httpServletRequest.reader
        val thirdCached = reader.lines().collect(Collectors.joining(System.lineSeparator()))

        // Then
        assertThat(isReady).isTrue
        assertThat(isFinishedBeforeFinish).isFalse
        assertThat(contents).isEqualTo(cached)
        assertThat(cached).isEqualTo(secondCached)
        assertThat(secondCached).isEqualTo(thirdCached)
        assertThat(isFinishedAfterFinish).isTrue
    }

    @Test
    @DisplayName("InputStream.read(ByteArray) 테스트")
    fun read_by_bytearray_using_inputStream() {

        // Given
        val contents = "Hello world"

        val mockHttpServletRequest = MockHttpServletRequest()
        mockHttpServletRequest.setContent(contents.toByteArray())
        val contentCachingRequestWrapper = CachingRequestWrapper(mockHttpServletRequest)

        // When
        val httpServletRequest: HttpServletRequest = contentCachingRequestWrapper
        val inputStream = httpServletRequest.inputStream

        val buffer = ByteArrayOutputStream()
        val byteArray = ByteArray(1024)
        var b: Int
        while (true) {
            b = inputStream.read(byteArray)
            if (b == -1) break
            buffer.write(byteArray, 0, b)
        }
        val cached = buffer.toString()

        // Then
        assertThat(cached).isEqualTo(contents)
    }

    @Test
    @DisplayName("InputStream.setReadListener() 테스트")
    fun setReadListener_of_inputStream() {

        // Given
        val contents = "Hello world"

        val mockHttpServletRequest = MockHttpServletRequest()
        mockHttpServletRequest.setContent(contents.toByteArray())
        val contentCachingRequestWrapper = CachingRequestWrapper(mockHttpServletRequest)

        // When
        val httpServletRequest: HttpServletRequest = contentCachingRequestWrapper
        val inputStream = httpServletRequest.inputStream
        val readListener: ReadListener = object: ReadListener {
            override fun onDataAvailable() {
                TODO("Not yet implemented")
            }

            override fun onAllDataRead() {
                TODO("Not yet implemented")
            }

            override fun onError(t: Throwable?) {
                TODO("Not yet implemented")
            }
        }

        val throwable = catchThrowable {
            inputStream.setReadListener(readListener)
        }

        // Then
        assertThat(throwable).isNotNull
        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
        assertThat(throwable).hasMessage("Not implemented yet")
    }
}
