package io.ankburov.mlbridge.filter.pre

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.Constants.CORRELATION_ID
import io.ankburov.mlbridge.utils.getCorrelationId
import io.ankburov.mlbridge.utils.getDataNode
import io.ankburov.mlbridge.utils.isMethod
import io.ankburov.mlbridge.wrapper.UpdateBodyRequestWrapper
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

/**
 * Extract from a canonical request model correlation id and data, and set data as a request body
 */
@Component
class TransformRequestFilter(private val objectMapper: ObjectMapper) : AbstractPreFilter() {
    override fun shouldFilter(): Boolean {
        return RequestContext.getCurrentContext().request.isMethod(HttpMethod.POST)
    }

    override fun filterOrder(): Int = Int.MIN_VALUE

    override fun run(): Any {
        return RequestContext.getCurrentContext().apply {
            if (request.contentLength == 0) {
                throw IllegalArgumentException("HTTP body cannot be empty")
            }
            val jsonTree = objectMapper.readTree(request.reader)
            val correlationId = jsonTree.getCorrelationId()?.takeUnless(String::isNullOrBlank)
                ?: throw IllegalArgumentException("CorrelationId must be present")
            this[CORRELATION_ID] = correlationId

            val dataNode = jsonTree.getDataNode()?.takeUnless { it.isNull }
                ?: throw IllegalArgumentException("Data must be present")
            val data = objectMapper.writeValueAsBytes(dataNode)

            request = request.changeRequestBody(data) // update the request body
        }
    }

    private fun HttpServletRequest.changeRequestBody(data: ByteArray) =
        UpdateBodyRequestWrapper(this, data)
}