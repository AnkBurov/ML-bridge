package io.ankburov.mlbridge.filter.post

import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.errorRaised
import io.ankburov.mlbridge.utils.isMethod
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

/**
 * Extract the body of a response from a disposable input stream to responseBody which can be read many times
 */
@Component
class ExtractResponseBodyFilter : AbstractPostFilter() {

    override fun shouldFilter(): Boolean {
        return with(RequestContext.getCurrentContext()) {
            request.isMethod(HttpMethod.POST) && !errorRaised()
        }
    }

    override fun filterOrder(): Int = Int.MIN_VALUE

    override fun run(): Any {
        return RequestContext.getCurrentContext().apply {
            val readText = (if (responseGZipped) GZIPInputStream(responseDataStream) else responseDataStream)
                ?.reader()
                ?.use(InputStreamReader::readText)
            if (!readText.isNullOrBlank()) {
                responseBody = readText
            }
        }
    }
}