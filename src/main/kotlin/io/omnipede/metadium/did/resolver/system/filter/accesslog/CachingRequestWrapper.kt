package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.apache.commons.io.IOUtils
import java.io.*
import java.nio.charset.StandardCharsets
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper


internal class CachingRequestWrapper(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {

    private val contentsAsByteArray: ByteArray

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        val bis = ByteArrayInputStream(contentsAsByteArray)
        return ContentCachingWrapperInputStream(bis)
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        val bis = ByteArrayInputStream(contentsAsByteArray)
        return BufferedReader(InputStreamReader(bis))
    }

    fun getBody(maxLength: Int): String {
        val buf = contentsAsByteArray
        return if (buf.size > maxLength) "TOO LONG CONTENTS" else String(
            buf,
            StandardCharsets.UTF_8
        )
    }

    private class ContentCachingWrapperInputStream(bis: InputStream) : ServletInputStream() {
        private val `is`: InputStream = bis

        override fun isFinished(): Boolean {
            return `is`.available() == 0
        }

        override fun isReady(): Boolean {
            return true
        }

        override fun setReadListener(readListener: ReadListener) {
            throw RuntimeException("Not implemented yet")
        }

        @Throws(IOException::class)
        override fun read(): Int {
            return `is`.read()
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int {
            return `is`.read(b)
        }

    }

    init {
        val `is`: InputStream = super.getInputStream()
        contentsAsByteArray = IOUtils.toByteArray(`is`)
    }
}
