package io.ankburov.mlbridge.wrapper

import org.springframework.mock.web.DelegatingServletInputStream
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class UpdateBodyRequestWrapper(
    request: HttpServletRequest,
    private val body: ByteArray,
    private val length: Int = body.size
) : HttpServletRequestWrapper(request) {


    override fun getInputStream(): ServletInputStream {
        return DelegatingServletInputStream(ByteArrayInputStream(body))
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    override fun getContentLength(): Int = length

    override fun getContentLengthLong(): Long = length.toLong()
}