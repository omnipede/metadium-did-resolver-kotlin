package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.apache.commons.io.output.TeeOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

class CachingResponseWrapper(
    httpServletResponse: HttpServletResponse
): HttpServletResponseWrapper(httpServletResponse) {

    private val byteArrayOutputStream = ByteArrayOutputStream()
    private val printStream = PrintStream(byteArrayOutputStream)

    fun getBody(maxLength: Int): String {
        return if (byteArrayOutputStream.size() > maxLength) "TOO LONG CONTENTS" else byteArrayOutputStream.toString()
    }

    override fun getOutputStream(): ServletOutputStream {
        return CachingOutputStream(super.getOutputStream(), printStream)
    }

    private inner class CachingOutputStream(
        one: OutputStream, two: OutputStream
    ): ServletOutputStream() {

        private val targetOutputStream = TeeOutputStream(one, two)

        @Throws(IOException::class)
        override fun write(b: Int) {
            targetOutputStream.write(b)
        }

        @Throws(IOException::class)
        override fun write(buf: ByteArray, off: Int, len: Int) {
            targetOutputStream.write(buf, off, len)
        }

        @Throws(IOException::class)
        override fun flush() {
            super.flush()
            targetOutputStream.flush()
        }

        @Throws(IOException::class)
        override fun close() {
            super.close()
            targetOutputStream.close()
        }

        override fun isReady(): Boolean {
            return true
        }

        override fun setWriteListener(writeListener: WriteListener?) {
            throw RuntimeException("Not implemented yet")
        }
    }
}
