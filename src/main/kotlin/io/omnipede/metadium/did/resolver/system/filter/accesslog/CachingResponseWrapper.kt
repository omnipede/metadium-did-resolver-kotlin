package io.omnipede.metadium.did.resolver.system.filter.accesslog

import org.apache.commons.io.output.TeeOutputStream
import org.springframework.mock.web.DelegatingServletOutputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import javax.servlet.ServletOutputStream
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
        return DelegatingServletOutputStream(TeeOutputStream(super.getOutputStream(), printStream))
    }

    override fun getWriter(): PrintWriter {
        return PrintWriter(DelegatingServletOutputStream(TeeOutputStream(super.getOutputStream(), printStream)))
    }
}
